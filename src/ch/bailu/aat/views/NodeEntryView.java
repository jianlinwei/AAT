package ch.bailu.aat.views;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.bailu.aat.coordinates.BoundingBox;
import ch.bailu.aat.gpx.GpxInformation;
import ch.bailu.aat.gpx.GpxPointNode;
import ch.bailu.aat.services.cache.CacheService;
import ch.bailu.aat.views.map.AbsOsmTileProvider;
import ch.bailu.aat.views.map.CachedTileProvider;
import ch.bailu.aat.views.map.OsmPreviewGenerator;
import ch.bailu.aat.views.map.OsmViewStatic;
import ch.bailu.aat.views.map.overlay.OsmOverlay;
import ch.bailu.aat.views.map.overlay.gpx.GpxDynOverlay;

public class NodeEntryView extends ViewGroup {

    private final OsmViewStatic map;
    private final TextView text;
    
    public NodeEntryView(Context context, CacheService cacheService, String key, int id) {
        super(context);

        AbsOsmTileProvider provider = new CachedTileProvider(context);
        map = new OsmViewStatic(context, provider);
        provider.setFileLoader(cacheService);
        
        final OsmOverlay[] overlays = new OsmOverlay[] {
                new GpxDynOverlay(map, cacheService ,id)
        };
        map.setOverlayList(overlays);

        text=new TextView(context);
        text.setTextColor(Color.WHITE);

        addView(map);
        addView(text);
    }


    @Override
    protected void onMeasure(int wspec, int hspec) {
        final int width  = MeasureSpec.getSize(wspec);
        final int wspecText = MeasureSpec.makeMeasureSpec(width-OsmPreviewGenerator.BITMAP_SIZE, MeasureSpec.EXACTLY);

        hspec = MeasureSpec.makeMeasureSpec(OsmPreviewGenerator.BITMAP_SIZE, MeasureSpec.EXACTLY);
        wspec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        
        map.measure(hspec, hspec);
        text.measure(wspecText, hspec);

        
        this.setMeasuredDimension(wspec, hspec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        text.layout(0, 0, text.getMeasuredWidth(), text.getMeasuredHeight());
        map.layout(text.getMeasuredWidth(), 0, text.getMeasuredWidth()+map.getMeasuredHeight(), map.getMeasuredHeight());
        
    }

    
    public void update(GpxInformation info, GpxPointNode node) {
        text.setText(Html.fromHtml(node.toHtml(getContext(), new StringBuilder()).toString()));
        final BoundingBox bounding = node.getBoundingBox();
        map.frameBoundingBox(bounding);
        map.updateGpxContent(info);

    }
}
