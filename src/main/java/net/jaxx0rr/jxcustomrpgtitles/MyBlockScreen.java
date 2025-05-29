package net.jaxx0rr.jxcustomrpgtitles;

import net.jaxx0rr.jxcustomrpgtitles.network.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import static net.jaxx0rr.jxcustomrpgtitles.JxCustomRPGTitles.MODID;

@OnlyIn(Dist.CLIENT)
public class MyBlockScreen extends AbstractContainerScreen<MyBlockMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/my_block_gui.png");
    private MultiLineEditBox textBox;
    private final MyCustomBlockEntity blockEntity;

    public MyBlockScreen(MyBlockMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        blockEntity = container.getBlockEntity();
        this.imageWidth = 650;
        this.imageHeight = 366;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Render a custom texture as the background
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);  // Renders the default background
        super.render(graphics, mouseX, mouseY, partialTick);
    }


    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Do nothing to suppress default labels like "Inventory"
        // graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        //graphics.drawString(this.font, "Activates on:", this.leftPos + 86, this.topPos - 23, 0x888888);
        //graphics.drawString(this.font, "Mode:", this.leftPos + 324, this.topPos - 23, 0x888888);
    }

    private void insertIntoTextBox(String text) {
        String existing = textBox.getValue();
        if (existing.equals("")) {
            textBox.setValue(text);
        } else {
            textBox.setValue(existing + "\n" + text);
        }
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(Button.builder(Component.literal("TITLE"), btn -> {
            insertIntoTextBox("/title @p times 10 70 20;/title @p title {\"text\":\"Welcome!\",\"color\":\"white\"}");
        }).bounds(this.leftPos + 10, this.topPos + 16, 40, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("NPC"), btn -> {
            //insertIntoTextBox("/execute positioned %d %d %d unless entity @e[type=minecraft:villager, name=\"Jimmy\", distance=..5] run summon minecraft:villager ~ ~ ~ {CustomName:'{\"text\":\"Jimmy\"}',NoAI:1b,Invulnerable:1b,VillagerData:{profession:toolsmith,level:2,type:plains},Offers:{Recipes:[{buy:{id:\"minecraft:dirt\",Count:1},sell:{id:\"minecraft:diamond\",Count:1},maxUses:9999999,rewardExp:false}]}}");
            insertIntoTextBox("/execute positioned %d %d %d unless entity @e[type=minecraft:villager, name=\"Jimmy\", distance=..5] run summon minecraft:villager ~ ~ ~ {CustomName:'{\"text\":\"Jimmy\"}',NoAI:1b,Invulnerable:1b,Rotation:[%d.0f,0.0f],VillagerData:{profession:toolsmith,level:2,type:plains},Offers:{Recipes:[{buy:{id:\"minecraft:dirt\",Count:1},sell:{id:\"minecraft:diamond\",Count:1},maxUses:9999999,rewardExp:false}]}}");
        }).bounds(this.leftPos + 60, this.topPos + 16, 40, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("LABEL"), btn -> {
            insertIntoTextBox("/execute positioned %d %d %d unless entity @e[type=armor_stand, tag=my_label, distance=..5] run summon armor_stand ~ ~ ~ {CustomName:'{\"text\":\"Welcome\"}',CustomNameVisible:1b,Invisible:1b,Marker:1b,Tags:[\"my_label\"]}");
        }).bounds(this.leftPos + 110, this.topPos + 16, 40, 20).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal(blockEntity.isUsingRedstone() ? "Redstone" : "Proximity"),
                        btn -> {
                            boolean newState = !blockEntity.isUsingRedstone(); // invert current state
                            blockEntity.setUsingRedstone(newState);
                            btn.setMessage(Component.literal(newState ? "Redstone" : "Proximity"));

                            ModMessages.INSTANCE.sendToServer(
                                    new UpdateRedstoneModePacket(blockEntity.getBlockPos(), newState)
                            );
                        })
                .bounds(this.leftPos + 260, this.topPos + 16, 60, 20)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.literal(blockEntity.isSingleUse() ? "Single" : "Repeat"),
                        btn -> {
                            boolean newVal = !blockEntity.isSingleUse();
                            blockEntity.setSingleUse(newVal);
                            btn.setMessage(Component.literal(newVal ? "Single" : "Repeat"));

                            ModMessages.INSTANCE.sendToServer(new UpdateSingleUsePacket(blockEntity.getBlockPos(), newVal));
                        })
                .bounds(this.leftPos + 340, this.topPos + 16, 40, 20)
                .build());


        if (Minecraft.getInstance().player != null) {
            MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
            boolean isCreative = gameMode != null && gameMode.getPlayerMode() == GameType.CREATIVE;

            if (isCreative) {
                this.addRenderableWidget(Button.builder(
                                Component.literal(blockEntity.isCreativeOnly() ? "Creative" : "Survival"),
                                btn -> {
                                    boolean newVal = !blockEntity.isCreativeOnly();
                                    blockEntity.setCreativeOnly(newVal);
                                    btn.setMessage(Component.literal(newVal ? "Creative" : "Survival"));

                                    ModMessages.INSTANCE.sendToServer(new UpdateCreativeOnlyPacket(blockEntity.getBlockPos(), newVal));
                                })
                        .bounds(this.leftPos + 400, this.topPos + 16, 50, 20)
                        .build());

                this.addRenderableWidget(Button.builder(
                                Component.literal("Reset History"),
                                btn -> {
                                    ModMessages.INSTANCE.sendToServer(
                                            new ClearTriggerHistoryPacket(blockEntity.getBlockPos()));
                                })
                        .bounds(this.leftPos + 470, this.topPos + 16, 80, 20)
                        .build());
            }
        }

        this.addRenderableWidget(Button.builder(Component.literal("CLEAR"), btn -> {
            textBox.setValue("");
        }).bounds(this.leftPos + 600, this.topPos + 16, 40, 20).build());

        this.textBox = new MultiLineEditBox(
                this.font,
                this.leftPos + 6,
                this.topPos + 36,
                640, // width
                320,  // height
                Component.literal("Command(s) (separated by ; or new line)"),
                Component.literal("") // initial value or suggestion
        );

        // Set initial value from BlockEntity
        //textBox.setValue(menu.blockEntity.getText());

        textBox.setValue(menu.syncedText);

        this.addRenderableWidget(textBox);



    }

    @Override
    public void onClose() {
        super.onClose();

        //menu.blockEntity.setText(textBox.getValue());
        //menu.blockEntity.sendUpdate(); // This is a custom method you’ll write

        ModMessages.INSTANCE.sendToServer(
                new UpdateCustomTextPacket(menu.blockEntity.getBlockPos(), textBox.getValue()));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true; // Esc still closes the screen
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        if (textBox.isFocused()) {
            // Handle clipboard shortcuts manually
            if (Screen.hasControlDown()) {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_C -> {
                        Minecraft.getInstance().keyboardHandler.setClipboard(textBox.getValue());
                        return true;
                    }
                    case GLFW.GLFW_KEY_V -> {
                        String paste = Minecraft.getInstance().keyboardHandler.getClipboard();
                        textBox.setValue(textBox.getValue() + paste);
                        return true;
                    }
                    case GLFW.GLFW_KEY_X -> {
                        Minecraft.getInstance().keyboardHandler.setClipboard(textBox.getValue());
                        textBox.setValue("");
                        return true;
                    }
                    case GLFW.GLFW_KEY_A -> {
                        // no selectAll support in MultiLineEditBox; might implement cursor control manually if needed
                        return true;
                    }
                }
            }

            if (textBox.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }

            // Block inventory key if focused
            if (keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    private boolean isAllowedWhileTyping(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_BACKSPACE ||
                keyCode == GLFW.GLFW_KEY_DELETE ||
                keyCode == GLFW.GLFW_KEY_LEFT ||
                keyCode == GLFW.GLFW_KEY_RIGHT ||
                keyCode == GLFW.GLFW_KEY_UP ||
                keyCode == GLFW.GLFW_KEY_DOWN ||
                keyCode == GLFW.GLFW_KEY_ENTER ||
                keyCode == GLFW.GLFW_KEY_TAB ||
                keyCode == GLFW.GLFW_KEY_LEFT_SHIFT ||
                keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT ||
                keyCode == GLFW.GLFW_KEY_LEFT_CONTROL ||
                keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (textBox != null && textBox.isFocused() && textBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

}

