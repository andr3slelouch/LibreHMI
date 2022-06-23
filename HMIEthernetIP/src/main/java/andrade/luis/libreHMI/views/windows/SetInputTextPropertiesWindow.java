package andrade.luis.libreHMI.views.windows;

import andrade.luis.libreHMI.views.SizeVBox;

public class SetInputTextPropertiesWindow extends SetTextPropertiesWindow{

    public SizeVBox getSizeVBox() {
        return sizeVBox;
    }

    public void setSizeVBox(SizeVBox sizeVBox) {
        this.sizeVBox = sizeVBox;
    }

    private SizeVBox sizeVBox;

    public SetInputTextPropertiesWindow(double width, double height) {
        super(340,275);
        sizeVBox = new SizeVBox(width,height,196,196);
        sizeVBox.getWidthValueHBox().setSpacing(29);
        sizeVBox.getHeightValueHBox().setSpacing(44);
        vbox.getChildren().add(0,sizeVBox);

    }
}
