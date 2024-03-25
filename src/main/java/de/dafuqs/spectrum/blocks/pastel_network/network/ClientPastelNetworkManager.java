package de.dafuqs.spectrum.blocks.pastel_network.network;

import com.mojang.blaze3d.vertex.PoseStack;
import de.dafuqs.spectrum.blocks.pastel_network.PastelRenderHelper;
import de.dafuqs.spectrum.blocks.pastel_network.nodes.PastelNodeBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ClientPastelNetworkManager implements PastelNetworkManager {
	
	private final List<PastelNetwork> networks = new ArrayList<>();
	
	@Override
	public PastelNetwork joinNetwork(PastelNodeBlockEntity node, UUID uuid) {
		PastelNetwork foundNetwork = null;
		for (int i = 0; i < this.networks.size(); i++) {
			PastelNetwork network = this.networks.get(i);
			if (network.getUUID().equals(uuid)) {
				network.addNode(node);
				foundNetwork = network;
			} else {
				if (network.removeNode(node, NodeRemovalReason.MOVED)) {
					i--;
				}
				// network empty => delete
				if (!network.hasNodes()) {
					this.networks.remove(network);
				}
			}
		}
		if (foundNetwork != null) {
			return foundNetwork;
		}
		
		PastelNetwork network = createNetwork(node.getLevel(), uuid);
		network.addNode(node);
		return network;
	}
	
	@Override
	public void removeNode(PastelNodeBlockEntity node, NodeRemovalReason reason) {
		PastelNetwork network = node.getNetwork();
		if (network != null) {
			network.removeNode(node, reason);
			if (network.nodes.size() == 0) {
				this.networks.remove(network);
			}
		}
	}
	
	private PastelNetwork createNetwork(Level world, UUID uuid) {
		PastelNetwork network = new PastelNetwork(world, uuid);
		this.networks.add(network);
		return network;
	}
	
	public void renderLines(WorldRenderContext context) {
		Minecraft client = Minecraft.getInstance();
		for (PastelNetwork network : this.networks) {
			if (network.getWorld().dimensionType() != context.world().dimensionType()) continue;
			Graph<PastelNodeBlockEntity, DefaultEdge> graph = network.getGraph();
			int color = network.getColor();
			float[] colors = PastelRenderHelper.unpackNormalizedColor(color);
			
			for (DefaultEdge edge : graph.edgeSet()) {
				PastelNodeBlockEntity source = graph.getEdgeSource(edge);
				PastelNodeBlockEntity target = graph.getEdgeTarget(edge);
				
				final PoseStack matrices = context.matrixStack();
				final Vec3 pos = context.camera().getPosition();
				matrices.pushPose();
				matrices.translate(-pos.x, -pos.y, -pos.z);
				PastelRenderHelper.renderLineTo(context.matrixStack(), context.consumers(), colors, source.getBlockPos(), target.getBlockPos());
				PastelRenderHelper.renderLineTo(context.matrixStack(), context.consumers(), colors, target.getBlockPos(), source.getBlockPos());
				
				if (client.options.renderDebug) {
					Vec3 offset = Vec3.atCenterOf(target.getBlockPos()).subtract(Vec3.atLowerCornerOf(source.getBlockPos()));
					Vec3 normalized = offset.normalize();
					Matrix4f positionMatrix = context.matrixStack().last().pose();
					PastelRenderHelper.renderDebugLine(context.consumers(), color, offset, normalized, positionMatrix);
				}
				matrices.popPose();
			}
		}
	}
	
}