package de.dafuqs.spectrum.inventories;

import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.blocks.particle_spawner.ParticleSpawnerBlockEntity;
import de.dafuqs.spectrum.blocks.particle_spawner.ParticleSpawnerConfiguration;
import de.dafuqs.spectrum.data_loaders.ParticleSpawnerParticlesDataLoader;
import de.dafuqs.spectrum.networking.SpectrumC2SPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class ParticleSpawnerScreen extends AbstractContainerScreen<ParticleSpawnerScreenHandler> {
	
	protected static final ResourceLocation GUI_TEXTURE = SpectrumCommon.locate("textures/gui/container/particle_spawner.png");
	protected static final int PARTICLES_PER_PAGE = 6;
	protected static final int TEXT_COLOR = 2236962;
	
	protected TextureAtlas spriteAtlasTexture;
	protected boolean glowing = false;
	protected boolean collisionsEnabled = false;
	protected int activeParticlePage = 0;
	protected int particleSelectionIndex = 0;
	protected boolean selectedParticleSupportsColoring = false;
	
	private final List<AbstractWidget> selectableWidgets = new ArrayList<>();
	private EditBox cyanField;
	private EditBox magentaField;
	private EditBox yellowField;
	private Button glowingButton;
	private EditBox amountField;
	private EditBox positionXField;
	private EditBox positionYField;
	private EditBox positionZField;
	private EditBox positionXVarianceField;
	private EditBox positionYVarianceField;
	private EditBox positionZVarianceField;
	private EditBox velocityXField;
	private EditBox velocityYField;
	private EditBox velocityZField;
	private EditBox velocityXVarianceField;
	private EditBox velocityYVarianceField;
	private EditBox velocityZVarianceField;
	private EditBox scale;
	private EditBox scaleVariance;
	private EditBox duration;
	private EditBox durationVariance;
	private EditBox gravity;
	private Button collisionsButton;
	private Button backButton;
	private Button forwardButton;
	private List<Button> particleButtons;
	
	private List<ParticleSpawnerParticlesDataLoader.ParticleSpawnerEntry> displayedParticleEntries = new ArrayList<>();
	
	public ParticleSpawnerScreen(ParticleSpawnerScreenHandler handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
		this.titleLabelX = 48;
		this.titleLabelY = 7;
		this.imageHeight = 243;
	}
	
	@Override
	protected void init() {
		super.init();

		this.spriteAtlasTexture = ((ParticleManagerAccessor) minecraft.particleEngine).getParticleAtlasTexture();
		this.displayedParticleEntries = ParticleSpawnerParticlesDataLoader.getAllUnlocked(minecraft.player);
		
		this.selectableWidgets.clear();
		setupInputFields(menu.getBlockEntity());
		setInitialFocus(amountField);
	}
	
	@Override
	public void containerTick() {
		super.containerTick();
		
		for (AbstractWidget widget : selectableWidgets) {
			if (widget instanceof EditBox textFieldWidget) {
				textFieldWidget.tick();
			}
		}
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			minecraft.player.closeContainer();
		}
		
		GuiEventListener focusedElement = getFocused();
		if (focusedElement instanceof EditBox focusedTextFieldWidget) {
			if (keyCode == GLFW.GLFW_KEY_TAB) {
				int currentIndex = selectableWidgets.indexOf(focusedElement);
				focusedTextFieldWidget.setFocused(false);
				
				if (modifiers == 1) {
					setFocused(selectableWidgets.get((selectableWidgets.size() + currentIndex - 1) % selectableWidgets.size()));
				} else {
					setFocused(selectableWidgets.get((currentIndex + 1) % selectableWidgets.size()));
				}
			}
			return focusedElement.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
		}
		
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
		renderBackground(drawContext);
		super.render(drawContext, mouseX, mouseY, delta);
		
		RenderSystem.disableBlend();
		renderForeground(drawContext, mouseX, mouseY, delta);
		renderTooltip(drawContext, mouseX, mouseY);
	}
	
	public void renderForeground(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
		for (AbstractWidget widget : selectableWidgets) {
			if (widget instanceof EditBox) {
				widget.render(drawContext, mouseX, mouseY, delta);
			}
		}
	}
	
	@Override
	protected void renderLabels(GuiGraphics drawContext, int mouseX, int mouseY) {
		var tr = this.font;
		drawContext.drawString(tr, this.title, this.titleLabelX, this.titleLabelY, 2236962, false);
		
		drawContext.drawString(tr, Component.literal("C").withStyle(ChatFormatting.AQUA), 7, 54, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.literal("M").withStyle(ChatFormatting.LIGHT_PURPLE), 47, 54, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.literal("Y").withStyle(ChatFormatting.GOLD), 90, 54, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.literal("Glow"), 130, 54, TEXT_COLOR, false);
		
		int offset = 23;
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.particle_count"), 10, 53 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.literal("x"), 66, 64 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.literal("y"), 99, 64 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.literal("z"), 134, 64 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.offset"), 10, 78 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.variance"), 21, 97 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.velocity"), 10, 117 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.variance"), 21, 137 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.scale"), 10, 161 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.variance"), 91, 161 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.duration"), 10, 181 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.variance"), 91, 181 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.gravity"), 10, 201 + offset, TEXT_COLOR, false);
		drawContext.drawString(tr, Component.translatable("block.spectrum.particle_spawner.collisions"), 90, 201 + offset, TEXT_COLOR, false);
	}
	
	@Override
	protected void renderBg(GuiGraphics drawContext, float delta, int mouseX, int mouseY) {
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		
		// the background
		drawContext.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
		
		// disabled coloring text field backgrounds
		if (!selectedParticleSupportsColoring) {
			drawContext.blit(GUI_TEXTURE, x + 15, y + 50, 214, 0, 31, 16);
			drawContext.blit(GUI_TEXTURE, x + 56, y + 50, 214, 0, 31, 16);
			drawContext.blit(GUI_TEXTURE, x + 97, y + 50, 214, 0, 31, 16);
		}
		
		// the checked & collision buttons checkmarks, if enabled
		if (collisionsEnabled) {
			drawContext.blit(GUI_TEXTURE, x + 146, y + 220, 176, 0, 16, 16);
		}
		if (glowing) {
			drawContext.blit(GUI_TEXTURE, x + 153, y + 50, 176, 0, 16, 16);
		}
		
		// particle selection outline
		if (particleSelectionIndex / PARTICLES_PER_PAGE == activeParticlePage) {
			drawContext.blit(GUI_TEXTURE, x + 27 + (20 * (particleSelectionIndex % PARTICLES_PER_PAGE)), y + 19, 192, 0, 22, 22);
		}
		
		RenderSystem.setShaderTexture(0, spriteAtlasTexture.location());
		int firstDisplayedEntryId = PARTICLES_PER_PAGE * activeParticlePage;
		for (int j = 0; j < PARTICLES_PER_PAGE; j++) {
			int spriteIndex = firstDisplayedEntryId + j;
			if (spriteIndex >= displayedParticleEntries.size()) {
				break;
			}
			TextureAtlasSprite particleSprite = spriteAtlasTexture.getSprite(displayedParticleEntries.get(spriteIndex).textureIdentifier());
			SpriteContents contents = particleSprite.contents();
			drawContext.blit(x + 38 + j * 20 - contents.width() / 2, y + 31 - contents.height() / 2, 0, contents.width(), contents.height(), particleSprite);
		}
	}
	
	protected void setupInputFields(ParticleSpawnerBlockEntity blockEntity) {
		int startX = (this.width - this.imageWidth) / 2 + 3;
		int startY = (this.height - this.imageHeight) / 2 + 3;
		
		ParticleSpawnerConfiguration configuration = blockEntity.getConfiguration();
		cyanField = addTextFieldWidget(startX + 16, startY + 51, Component.literal("Cyan"), String.valueOf(configuration.getCmyColor().x()), this::isPositiveDecimalNumber100);
		magentaField = addTextFieldWidget(startX + 57, startY + 51, Component.literal("Magenta"), String.valueOf(configuration.getCmyColor().y()), this::isPositiveDecimalNumber100);
		yellowField = addTextFieldWidget(startX + 97, startY + 51, Component.literal("Yellow"), String.valueOf(configuration.getCmyColor().z()), this::isPositiveDecimalNumber100);
		glowingButton = Button.builder(Component.translatable("gui.spectrum.button.glowing"), this::glowingButtonPressed)
				.size(16, 16)
				.pos(startX + 153, startY + 50)
				.build();
		addWidget(glowingButton);
		this.glowing = configuration.glows();
		
		int offset = 23;
		amountField = addTextFieldWidget(startX + 110, startY + 50 + offset, Component.literal("Particles per Second"), String.valueOf(configuration.getParticlesPerSecond()), this::isPositiveDecimalNumberUnderThousand);
		positionXField = addTextFieldWidget(startX + 61, startY + 74 + offset, Component.literal("X Position"), String.valueOf(configuration.getSourcePosition().x()), this::isAbsoluteDecimalNumberThousand);
		positionYField = addTextFieldWidget(startX + 96, startY + 74 + offset, Component.literal("Y Position"), String.valueOf(configuration.getSourcePosition().y()), this::isAbsoluteDecimalNumberThousand);
		positionZField = addTextFieldWidget(startX + 131, startY + 74 + offset, Component.literal("Z Position"), String.valueOf(configuration.getSourcePosition().z()), this::isAbsoluteDecimalNumberThousand);
		positionXVarianceField = addTextFieldWidget(startX + 69, startY + 94 + offset, Component.literal("X Position Variance"), String.valueOf(configuration.getSourcePositionVariance().x()), this::isAbsoluteDecimalNumberThousand);
		positionYVarianceField = addTextFieldWidget(startX + 104, startY + 94 + offset, Component.literal("Y Position Variance"), String.valueOf(configuration.getSourcePositionVariance().y()), this::isAbsoluteDecimalNumberThousand);
		positionZVarianceField = addTextFieldWidget(startX + 140, startY + 94 + offset, Component.literal("Z Position Variance"), String.valueOf(configuration.getSourcePositionVariance().z()), this::isAbsoluteDecimalNumberThousand);
		velocityXField = addTextFieldWidget(startX + 61, startY + 114 + offset, Component.literal("X Velocity"), String.valueOf(configuration.getVelocity().x()), this::isAbsoluteDecimalNumberThousand);
		velocityYField = addTextFieldWidget(startX + 96, startY + 114 + offset, Component.literal("Y Velocity"), String.valueOf(configuration.getVelocity().y()), this::isAbsoluteDecimalNumberThousand);
		velocityZField = addTextFieldWidget(startX + 131, startY + 114 + offset, Component.literal("Z Velocity"), String.valueOf(configuration.getVelocity().z()), this::isAbsoluteDecimalNumberThousand);
		velocityXVarianceField = addTextFieldWidget(startX + 69, startY + 134 + offset, Component.literal("X Velocity Variance"), String.valueOf(configuration.getVelocityVariance().x()), this::isAbsoluteDecimalNumberThousand);
		velocityYVarianceField = addTextFieldWidget(startX + 104, startY + 134 + offset, Component.literal("Y Velocity Variance"), String.valueOf(configuration.getVelocityVariance().y()), this::isAbsoluteDecimalNumberThousand);
		velocityZVarianceField = addTextFieldWidget(startX + 140, startY + 134 + offset, Component.literal("Z Velocity Variance"), String.valueOf(configuration.getVelocityVariance().z()), this::isAbsoluteDecimalNumberThousand);
		scale = addTextFieldWidget(startX + 55, startY + 158 + offset, Component.literal("Scale"), String.valueOf(configuration.getScale()), this::isPositiveDecimalNumberUnderTen);
		scaleVariance = addTextFieldWidget(startX + 139, startY + 158 + offset, Component.literal("Scale Variance"), String.valueOf(configuration.getScaleVariance()), this::isPositiveDecimalNumberUnderTen);
		duration = addTextFieldWidget(startX + 55, startY + 178 + offset, Component.literal("Duration"), String.valueOf(configuration.getLifetimeTicks()), this::isPositiveWholeNumberUnderThousand);
		durationVariance = addTextFieldWidget(startX + 139, startY + 178 + offset, Component.literal("Duration Variance"), String.valueOf(configuration.getLifetimeVariance()), this::isPositiveWholeNumberUnderThousand);
		gravity = addTextFieldWidget(startX + 55, startY + 198 + offset, Component.literal("Gravity"), String.valueOf(configuration.getGravity()), this::isBetweenZeroAndOne);
		
		collisionsButton = Button.builder(Component.translatable("gui.spectrum.button.collisions"), this::collisionButtonPressed)
				.pos(startX + 142, startY + 194 + offset)
				.size(16, 16)
				.build();
		collisionsEnabled = configuration.hasCollisions();
		addWidget(collisionsButton);
		
		selectableWidgets.add(cyanField);
		selectableWidgets.add(magentaField);
		selectableWidgets.add(yellowField);
		selectableWidgets.add(glowingButton);
		selectableWidgets.add(amountField);
		selectableWidgets.add(positionXField);
		selectableWidgets.add(positionYField);
		selectableWidgets.add(positionZField);
		selectableWidgets.add(positionXVarianceField);
		selectableWidgets.add(positionYVarianceField);
		selectableWidgets.add(positionZVarianceField);
		selectableWidgets.add(velocityXField);
		selectableWidgets.add(velocityYField);
		selectableWidgets.add(velocityZField);
		selectableWidgets.add(velocityXVarianceField);
		selectableWidgets.add(velocityYVarianceField);
		selectableWidgets.add(velocityZVarianceField);
		selectableWidgets.add(scale);
		selectableWidgets.add(scaleVariance);
		selectableWidgets.add(duration);
		selectableWidgets.add(durationVariance);
		selectableWidgets.add(gravity);
		selectableWidgets.add(collisionsButton);
		
		backButton = Button.builder(Component.translatable("gui.spectrum.button.back"), this::navigationButtonPressed)
				.size(12, 14)
				.pos(startX + 11, startY + 19)
				.build();
		addWidget(backButton);
		forwardButton = Button.builder(Component.translatable("gui.spectrum.button.forward"), this::navigationButtonPressed)
				.size(12, 14)
				.pos(startX + 147, startY + 19)
				.build();
		addWidget(forwardButton);
		
		particleButtons = List.of(
				addParticleButton(startX + 23, startY + 16),
				addParticleButton(startX + 23 + 20, startY + 16),
				addParticleButton(startX + 23 + 40, startY + 16),
				addParticleButton(startX + 23 + 60, startY + 16),
				addParticleButton(startX + 23 + 80, startY + 16),
				addParticleButton(startX + 23 + 100, startY + 16)
		);
		
		this.particleSelectionIndex = 0;
		int particleIndex = 0;
		for (ParticleSpawnerParticlesDataLoader.ParticleSpawnerEntry availableParticle : displayedParticleEntries) {
			if (availableParticle.particleType().equals(configuration.getParticleType())) {
				this.particleSelectionIndex = particleIndex;
				break;
			}
			particleIndex++;
		}
		
		if (displayedParticleEntries.size() == 0) {
			setColoringEnabled(false);
		}
		
		ParticleSpawnerParticlesDataLoader.ParticleSpawnerEntry entry = displayedParticleEntries.get(this.particleSelectionIndex);
		setColoringEnabled(entry.supportsColoring());
	}
	
	private void navigationButtonPressed(Button buttonWidget) {
		int pageCount = displayedParticleEntries.size() / PARTICLES_PER_PAGE;
		if (buttonWidget == forwardButton) {
			activeParticlePage = (activeParticlePage + 1) % pageCount;
		} else {
			activeParticlePage = (activeParticlePage - 1 + pageCount) % pageCount;
		}
	}
	
	private @NotNull EditBox addTextFieldWidget(int x, int y, Component text, String defaultText, Predicate<String> textPredicate) {
		EditBox textFieldWidget = new EditBox(this.font, x, y, 31, 16, text);
		
		textFieldWidget.setFilter(textPredicate);
		textFieldWidget.setCanLoseFocus(true);
		textFieldWidget.setEditable(true);
		textFieldWidget.setTextColor(-1);
		textFieldWidget.setTextColorUneditable(-1);
		textFieldWidget.setBordered(false);
		textFieldWidget.setMaxLength(6);
		textFieldWidget.setValue(defaultText);
		textFieldWidget.setResponder(this::onTextBoxValueChanged);
		addWidget(textFieldWidget);
		
		return textFieldWidget;
	}
	
	private @NotNull Button addParticleButton(int x, int y) {
		Button button = Button.builder(Component.translatable("gui.spectrum.button.particles"), this::particleButtonPressed)
				.size(20, 20)
				.pos(x, y)
				.build();
		addWidget(button);
		return button;
	}
	
	private void particleButtonPressed(Button buttonWidget) {
		int buttonIndex = particleButtons.indexOf(buttonWidget);
		int newIndex = PARTICLES_PER_PAGE * activeParticlePage + buttonIndex;

		ParticleSpawnerParticlesDataLoader.ParticleSpawnerEntry entry = displayedParticleEntries.get(newIndex);
		setColoringEnabled(entry.supportsColoring());

		if (newIndex < displayedParticleEntries.size()) {
			particleSelectionIndex = newIndex;
			onValuesChanged();
		}
	}
	
	private void setColoringEnabled(boolean enabled) {
		this.selectedParticleSupportsColoring = enabled;
		
		this.cyanField.setEditable(enabled);
		this.magentaField.setEditable(enabled);
		this.yellowField.setEditable(enabled);
		this.cyanField.setCanLoseFocus(enabled);
		this.magentaField.setCanLoseFocus(enabled);
		this.yellowField.setCanLoseFocus(enabled);
		this.cyanField.setResponder(enabled ? this::onTextBoxValueChanged : null);
		this.magentaField.setResponder(enabled ? this::onTextBoxValueChanged : null);
		this.yellowField.setResponder(enabled ? this::onTextBoxValueChanged : null);
		
		this.setFocused(this.amountField);
	}
	
	private void collisionButtonPressed(Button buttonWidget) {
		collisionsEnabled = !collisionsEnabled;
		this.onValuesChanged();
	}
	
	private void glowingButtonPressed(Button buttonWidget) {
		glowing = !glowing;
		this.onValuesChanged();
	}
	
	private void onTextBoxValueChanged(@NotNull String newValue) {
		onValuesChanged();
	}
	
	protected boolean isDecimalNumber(@NotNull String text) {
		return text.matches("^(-)?\\d*+(?:\\.\\d*)?$");
	}
	
	private boolean isPositiveDecimalNumberUnderThousand(String text) {
		try {
			return Double.parseDouble(text) < 1000;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean isAbsoluteDecimalNumberThousand(String text) {
		try {
			return Math.abs(Double.parseDouble(text)) < 1000;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean isPositiveDecimalNumber100(String text) {
		try {
			int number = Integer.parseInt(text);
			return number >= 0 && number <= 100;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean isPositiveDecimalNumberUnderTen(String text) {
		try {
			return Double.parseDouble(text) < 10;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	protected boolean isPositiveDecimalNumber(@NotNull String text) {
		return text.matches("^\\d*+(?:\\.\\d*)?$");
	}
	
	protected boolean isPositiveWholeNumber(@NotNull String text) {
		return text.matches("^\\d*$");
	}
	
	protected boolean isPositiveWholeNumberUnderThousand(@NotNull String text) {
		try {
			return Integer.parseInt(text) < 1000;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	protected boolean isBetweenZeroAndOne(@NotNull String text) {
		try {
			float f = Float.parseFloat(text);
			return f >= 0 && f <= 1;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Send these changes to the server to distribute to all clients
	 */
	private void onValuesChanged() {
		try {
			ParticleSpawnerConfiguration configuration = new ParticleSpawnerConfiguration(
					displayedParticleEntries.get(particleSelectionIndex).particleType(),
					selectedParticleSupportsColoring ? new Vector3i(Float.parseFloat(cyanField.getValue()), Float.parseFloat(magentaField.getValue()), Float.parseFloat(yellowField.getValue()), RoundingMode.HALF_DOWN) : new Vector3i(0, 0, 0),
					glowing,
					Float.parseFloat(amountField.getValue()),
					new Vector3f(Float.parseFloat(positionXField.getValue()), Float.parseFloat(positionYField.getValue()), Float.parseFloat(positionZField.getValue())),
					new Vector3f(Float.parseFloat(positionXVarianceField.getValue()), Float.parseFloat(positionYVarianceField.getValue()), Float.parseFloat(positionZVarianceField.getValue())),
					new Vector3f(Float.parseFloat(velocityXField.getValue()), Float.parseFloat(velocityYField.getValue()), Float.parseFloat(velocityZField.getValue())),
					new Vector3f(Float.parseFloat(velocityXVarianceField.getValue()), Float.parseFloat(velocityYVarianceField.getValue()), Float.parseFloat(velocityZVarianceField.getValue())),
					Float.parseFloat(scale.getValue()),
					Float.parseFloat(scaleVariance.getValue()),
					Integer.parseInt(duration.getValue()),
					Integer.parseInt(durationVariance.getValue()),
					Float.parseFloat(gravity.getValue()),
					collisionsEnabled
			);
			
			FriendlyByteBuf packetByteBuf = PacketByteBufs.create();
			configuration.write(packetByteBuf);
			
			ClientPlayNetworking.send(SpectrumC2SPackets.CHANGE_PARTICLE_SPAWNER_SETTINGS_PACKET_ID, packetByteBuf);
		} catch (Exception e) {
			// the text boxes currently are not able to be parsed yet.
			// wait until everything is set up
		}
	}
	
}