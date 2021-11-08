package bleach.a32k.settings;

public class SettingToggle extends SettingBase
{
    public boolean state;
    public String text;
    
    public SettingToggle(final boolean state, final String text) {
        this.state = state;
        this.text = text;
    }
}
