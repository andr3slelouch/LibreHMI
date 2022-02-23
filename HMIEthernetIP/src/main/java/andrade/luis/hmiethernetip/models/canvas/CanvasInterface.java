package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.GraphicalRepresentationData;
import andrade.luis.hmiethernetip.models.Tag;

public interface CanvasInterface {
    void delete(GraphicalRepresentationData graphicalRepresentationData);
    Tag selectTag();
}
