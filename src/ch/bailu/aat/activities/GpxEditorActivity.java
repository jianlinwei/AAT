package ch.bailu.aat.activities;

import java.io.File;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import ch.bailu.aat.R;
import ch.bailu.aat.description.ContentDescription;
import ch.bailu.aat.description.DescriptionInterface;
import ch.bailu.aat.description.DistanceDescription;
import ch.bailu.aat.description.NameDescription;
import ch.bailu.aat.description.PathDescription;
import ch.bailu.aat.description.TrackSizeDescription;
import ch.bailu.aat.dispatcher.ContentDispatcher;
import ch.bailu.aat.dispatcher.ContentSource;
import ch.bailu.aat.dispatcher.CurrentFileSource;
import ch.bailu.aat.dispatcher.CurrentLocationSource;
import ch.bailu.aat.dispatcher.EditorSource;
import ch.bailu.aat.dispatcher.OverlaySource;
import ch.bailu.aat.dispatcher.TrackerSource;
import ch.bailu.aat.gpx.GpxInformation;
import ch.bailu.aat.helpers.AppDialog;
import ch.bailu.aat.helpers.AppLayout;
import ch.bailu.aat.helpers.AppLog;
import ch.bailu.aat.helpers.Timer;
import ch.bailu.aat.services.cache.CacheService;
import ch.bailu.aat.services.dem.ElevationService;
import ch.bailu.aat.services.directory.DirectoryService;
import ch.bailu.aat.services.editor.EditorInterface;
import ch.bailu.aat.services.editor.EditorService;
import ch.bailu.aat.services.tracker.TrackerService;
import ch.bailu.aat.views.BusyIndicator;
import ch.bailu.aat.views.ContentView;
import ch.bailu.aat.views.ControlBar;
import ch.bailu.aat.views.MultiView;
import ch.bailu.aat.views.NodeListView;
import ch.bailu.aat.views.SummaryListView;
import ch.bailu.aat.views.TrackDescriptionView;
import ch.bailu.aat.views.VerticalView;
import ch.bailu.aat.views.graph.DistanceAltitudeGraphView;
import ch.bailu.aat.views.map.OsmInteractiveView;
import ch.bailu.aat.views.map.overlay.CurrentLocationOverlay;
import ch.bailu.aat.views.map.overlay.OsmOverlay;
import ch.bailu.aat.views.map.overlay.control.EditorOverlay;
import ch.bailu.aat.views.map.overlay.control.InformationBarOverlay;
import ch.bailu.aat.views.map.overlay.control.NavigationBarOverlay;
import ch.bailu.aat.views.map.overlay.gpx.GpxDynOverlay;
import ch.bailu.aat.views.map.overlay.gpx.GpxOverlayListOverlay;
import ch.bailu.aat.views.map.overlay.grid.GridDynOverlay;

public class GpxEditorActivity extends AbsDispatcher
implements OnClickListener,  Runnable {

    private static final Class<?> SERVICES[] = {
        TrackerService.class, 
        DirectoryService.class, 
        ElevationService.class,
        EditorService.class,
        CacheService.class,
    };

    private final LayoutParams layout= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    private static final String SOLID_KEY="gpx_editor";

    private ImageButton nextView, nextFile, previousFile;


    private BusyIndicator busyIndicator;
    private MultiView     multiView;
    private OsmInteractiveView    mapView;
    private Timer timer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timer = new Timer(this,50);

        connectToServices(SERVICES);
    }


    private MultiView createMultiView() {
        mapView = new OsmInteractiveView(getServiceContext(), SOLID_KEY);

        OsmOverlay overlayList[] = {
                new GpxOverlayListOverlay(mapView, getServiceContext().getCacheService()),
                new GpxDynOverlay(mapView, getServiceContext().getCacheService(), GpxInformation.ID.INFO_ID_TRACKER), 
                new GridDynOverlay(mapView, getServiceContext().getElevationService()),
                new CurrentLocationOverlay(mapView),
                new EditorOverlay(mapView, getServiceContext().getCacheService(), INFO_ID_EDITOR_OVERLAY, getServiceContext().getEditorService().getOverlayEditor(), getServiceContext().getElevationService()),
                new NavigationBarOverlay(mapView),
                new InformationBarOverlay(mapView)
        };


        mapView.setOverlayList(overlayList);


        ContentDescription summaryData[] = {
                new NameDescription(this),
                new PathDescription(this),
                new DistanceDescription(this),
                new TrackSizeDescription(this),
        };


        NodeListView wayList = new NodeListView(getServiceContext(),
                SOLID_KEY,
                INFO_ID_EDITOR_OVERLAY 
                );

        TrackDescriptionView viewData[] = {

                wayList,
                mapView,

                new VerticalView(this, SOLID_KEY, INFO_ID_EDITOR_OVERLAY, new TrackDescriptionView[] {
                        new DistanceAltitudeGraphView(this, SOLID_KEY),
                        new SummaryListView(this, SOLID_KEY, INFO_ID_EDITOR_OVERLAY, summaryData)})
        };   

        return new MultiView(this, SOLID_KEY, INFO_ID_ALL, viewData);
    }



    private ControlBar createButtonBar() {
        ControlBar bar = new ControlBar(
                this, 
                AppLayout.getOrientationAlongSmallSide(this));

        nextView = bar.addImageButton(R.drawable.go_next_inverse);
        previousFile =  bar.addImageButton(R.drawable.go_up_inverse);
        nextFile = bar.addImageButton(R.drawable.go_down_inverse);

        busyIndicator = new BusyIndicator(this, INFO_ID_EDITOR_OVERLAY);
        bar.addView(busyIndicator);

        bar.setOrientation(AppLayout.getOrientationAlongSmallSide(this));
        bar.setOnClickListener1(this);

        return bar;
    }


    @Override
    public void onDestroy() {
        timer.close();
        super.onDestroy();
    }


    @Override
    public void onPause() {

        getServiceContext().getDirectoryService().storePosition();

        super.onPause();
    }


    @Override
    public void onServicesUp() {
        ContentView contentView = new ContentView(this);
        contentView.addView(createButtonBar(), layout);


        multiView = createMultiView();
        contentView.addView(multiView, layout);

        setContentView(contentView);



        DescriptionInterface[] target = new DescriptionInterface[] {
                multiView,this, busyIndicator
        };


        ContentSource[] source = new ContentSource[] {
                new EditorSource(getServiceContext(), INFO_ID_EDITOR_OVERLAY),
                new CurrentLocationSource(getServiceContext()),
                new TrackerSource(getServiceContext()),
                new OverlaySource(getServiceContext()), 
                new CurrentFileSource(getServiceContext())
        };

        setDispatcher(new ContentDispatcher(this,source, target));

        timer.kick();
    }



    private void showCurrentFile() {
        getServiceContext().getEditorService().editOverlay(
                new File(getServiceContext().getDirectoryService().getCurrent().getPath()));
        mapView.frameBoundingBox(getServiceContext().getDirectoryService().getCurrent().getBoundingBox());
        getDispatcher().forceUpdate();
    }




    private GpxInformation getEditorInfo() {
        return getServiceContext().getEditorService().getOverlayInformation();
    }


    @Override
    public void onBackPressed() {
        try {
            final EditorInterface editor = getServiceContext().getEditorService().getOverlayEditor();

            if (editor.isModified()) {
                new AppDialog() {
                    @Override
                    protected void onPositiveClick() {

                        editor.save();
                        closeActivity();
                    }

                    @Override
                    public void onNeutralClick() {
                        editor.discard();
                        closeActivity();
                    }


                }.displaySaveDiscardDialog(this, getEditorInfo().getName());
            } else {
                closeActivity();
            }

        } catch (Exception e) {
            AppLog.e(GpxEditorActivity.this, e);
            closeActivity();
        }

    }


    private void closeActivity() {
        super.onBackPressed();
    }


    @Override
    public void onClick(final View v) {
        try {
            final EditorInterface editor = getServiceContext().getEditorService().getOverlayEditor();

            if (v == previousFile || v ==nextFile) {
                if (editor.isModified()) {
                    new AppDialog() {
                        @Override
                        protected void onPositiveClick() {
                            editor.save();
                            switchFile(v);
                        }

                        @Override
                        public void onNeutralClick() {
                            editor.discard();
                            switchFile(v);
                        }


                    }.displaySaveDiscardDialog(this, getEditorInfo().getName());
                } else {
                    switchFile(v);
                }

            } else if (v == nextView) {
                multiView.setNext();

            }
        } catch (Exception e) {
            AppLog.e(this, e);
        }
    }



    private void switchFile(View v) {
        if (v==nextFile)
            getServiceContext().getDirectoryService().toNext();
        else if (v==previousFile)
            getServiceContext().getDirectoryService().toPrevious();

        showCurrentFile();
    }


    @Override
    public void run() {
        showCurrentFile();
    }
}
