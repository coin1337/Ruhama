package bleach.a32k.mixin.mixins;

import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.inventory.*;
import bleach.a32k.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ GuiContainer.class })
public class MixinContainerGui extends GuiScreen
{
    MapPeek peek;
    
    public MixinContainerGui() {
        this.peek = new MapPeek();
    }
    
    @Inject(method = { "drawScreen(IIF)V" }, at = { @At("RETURN") })
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo info) {
        try {
            this.peek.draw(mouseX, mouseY, (GuiContainer)this.mc.currentScreen);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
