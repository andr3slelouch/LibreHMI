package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.views.SizeVBox;

/**
 * Ventana de definición de propiedades de texto de entrada
 */
public class SetInputTextPropertiesWindow extends SetTextPropertiesWindow{

    public SizeVBox getSizeVBox() {
        return sizeVBox;
    }

    public void setSizeVBox(SizeVBox sizeVBox) {
        this.sizeVBox = sizeVBox;
    }

    private SizeVBox sizeVBox;

    /**
     * Constructor de la ventana
     * @param width Ancho de la ventana
     * @param height Altura de la ventana
     */
    public SetInputTextPropertiesWindow(double width, double height) {
        super(340,275);
        sizeVBox = new SizeVBox(width,height,196,196);
        sizeVBox.getWidthValueHBox().setSpacing(29);
        sizeVBox.getHeightValueHBox().setSpacing(44);
        vbox.getChildren().add(0,sizeVBox);

    }
}
