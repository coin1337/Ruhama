package bleach.a32k.gui;

public class AdvancedText
{
    public String text;
    public boolean shadow;
    public int color;
    
    public AdvancedText(final String text) {
        this.text = "";
        this.shadow = true;
        this.color = -1;
        this.text = text;
    }
    
    public AdvancedText(final String text, final boolean shadow, final int color) {
        this.text = "";
        this.shadow = true;
        this.color = -1;
        this.text = text;
        this.shadow = shadow;
        this.color = color;
    }
}
