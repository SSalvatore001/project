//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.view;

import pp.droids.model.item.Item;
import pp.droids.notifications.GameEventListener;
import pp.droids.notifications.ItemAddedEvent;
import pp.droids.notifications.ItemDestroyedEvent;
import pp.droids.notifications.MapChangedEvent;
import pp.view.ModelViewSynchronizer;

public class SyncListener implements GameEventListener {
    private final ModelViewSynchronizer<Item> sync;

    public SyncListener(ModelViewSynchronizer<Item> sync) {
        this.sync = sync;
    }

    @Override
    public void received(ItemDestroyedEvent event) {
        sync.delete(event.item());
    }

    @Override
    public void received(ItemAddedEvent added) {
        sync.add(added.item());
    }

    @Override
    public void received(MapChangedEvent event) {
        sync.clear();
        event.newMap().getItems().forEach(sync::add);
    }
}