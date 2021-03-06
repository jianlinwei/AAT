package ch.bailu.aat.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import ch.bailu.aat.R;
import ch.bailu.aat.description.AverageSpeedDescription;
import ch.bailu.aat.description.CaloriesDescription;
import ch.bailu.aat.description.ContentDescription;
import ch.bailu.aat.description.DateDescription;
import ch.bailu.aat.description.DescriptionInterface;
import ch.bailu.aat.description.DistanceDescription;
import ch.bailu.aat.description.EndDateDescription;
import ch.bailu.aat.description.MaximumSpeedDescription;
import ch.bailu.aat.description.NameDescription;
import ch.bailu.aat.description.PathDescription;
import ch.bailu.aat.description.PauseDescription;
import ch.bailu.aat.description.TimeDescription;
import ch.bailu.aat.description.TrackSizeDescription;
import ch.bailu.aat.dispatcher.ContentDispatcher;
import ch.bailu.aat.dispatcher.ContentSource;
import ch.bailu.aat.dispatcher.CurrentFileSource;
import ch.bailu.aat.dispatcher.CurrentLocationSource;
import ch.bailu.aat.dispatcher.EditorSource;
import ch.bailu.aat.dispatcher.OverlaySource;
import ch.bailu.aat.dispatcher.TrackerSource;
import ch.bailu.aat.gpx.GpxInformation;
import ch.bailu.aat.helpers.AppLayout;
import ch.bailu.aat.helpers.AppLog;
import ch.bailu.aat.services.MultiServiceLink.ServiceNotUpException;
import ch.bailu.aat.services.cache.CacheService;
import ch.bailu.aat.services.dem.ElevationService;
import ch.bailu.aat.services.directory.DirectoryService;
import ch.bailu.aat.services.editor.EditorService;
import ch.bailu.aat.services.overlay.OverlayService;
import ch.bailu.aat.services.tracker.TrackerService;
import ch.bailu.aat.views.BusyIndicator;
import ch.bailu.aat.views.ContentView;
import ch.bailu.aat.views.ControlBar;
import ch.bailu.aat.views.MultiView;
import ch.bailu.aat.views.SummaryListView;
import ch.bailu.aat.views.TrackDescriptionView;
import ch.bailu.aat.views.VerticalView;
import ch.bailu.aat.views.graph.DistanceAltitudeGraphView;
import ch.bailu.aat.views.graph.DistanceSpeedGraphView;
import ch.bailu.aat.views.map.OsmInteractiveView;
import ch.bailu.aat.views.map.overlay.CurrentLocationOverlay;
import ch.bailu.aat.views.map.overlay.OsmOverlay;
import ch.bailu.aat.views.map.overlay.control.EditorOverlay;
import ch.bailu.aat.views.map.overlay.control.InformationBarOverlay;
import ch.bailu.aat.views.map.overlay.control.NavigationBarOverlay;
import ch.bailu.aat.views.map.overlay.gpx.GpxDynOverlay;
import ch.bailu.aat.views.map.overlay.gpx.GpxOverlayListOverlay;
import ch.bailu.aat.views.map.overlay.grid.GridDynOverlay;


public class FileContentActivity extends AbsDispatcher implements OnClickListener{
    public static final Class<?> SERVICES[] = {
        TrackerService.class, 
        OverlayService.class,
        ElevationService.class,
        EditorService.class,
        CacheService.class,
        DirectoryService.class
    };
    
    
    private static final String SOLID_KEY="file_content";

    private ImageButton nextView, nextFile, previousFile;


    private LinearLayout contentView;

    private BusyIndicator      busyIndicator;
    private MultiView          multiView;
    private OsmInteractiveView map;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentView = new ContentView(this);
        contentView.addView(createButtonBar());
        multiView = createMultiView();
        contentView.addView(multiView);
        setContentView(contentView);

        connectToServices(SERVICES);
    }


    private ControlBar createButtonBar() {
        ControlBar bar = new ControlBar(
                this, 
                AppLayout.getOrientationAlongSmallSide(this));

        nextView = bar.addImageButton(R.drawable.go_next_inverse);
        previousFile =  bar.addImageButton(R.drawable.go_up_inverse);
        nextFile = bar.addImageButton(R.drawable.go_down_inverse);

        busyIndicator = new BusyIndicator(this, GpxInformation.ID.INFO_ID_FILEVIEW);
        bar.addView(busyIndicator);

        bar.setOrientation(AppLayout.getOrientationAlongSmallSide(this));
        bar.setOnClickListener1(this);
        return bar;
    }


    private MultiView createMultiView() {
        map = new OsmInteractiveView(this, SOLID_KEY);



        ContentDescription summaryData[] = {
                new NameDescription(this),
                new PathDescription(this),
                new TimeDescription(this),
                new DateDescription(this),
                new EndDateDescription(this),
                new PauseDescription(this),
                new DistanceDescription(this),
                new AverageSpeedDescription(this),
                new MaximumSpeedDescription(this),
                new CaloriesDescription(this),
                new TrackSizeDescription(this),
        };

        TrackDescriptionView viewData[] = {
                new SummaryListView(this, SOLID_KEY, INFO_ID_FILEVIEW, summaryData), 
                map,
                new VerticalView(this, SOLID_KEY, INFO_ID_FILEVIEW, new TrackDescriptionView[] {
                        new DistanceAltitudeGraphView(this, SOLID_KEY),
                        new DistanceSpeedGraphView(this, SOLID_KEY)})
        };   

        return new MultiView(this, SOLID_KEY, INFO_ID_ALL, viewData);
    }


    @Override
    public void onPause() {
        //AppLog.d(this, "onPause()");
        try {
            getDirectoryService().storePosition();
        } catch (ServiceNotUpException e) {
            AppLog.e(this, e);
        }

        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onServicesUp() {
        try {
            map.setServices(getCacheService());

            OsmOverlay overlayList[] = {
                    new GpxOverlayListOverlay(map, getCacheService()),
                    new GpxDynOverlay(map, getCacheService(), GpxInformation.ID.INFO_ID_TRACKER), 
                    new GpxDynOverlay(map, getCacheService(), GpxInformation.ID.INFO_ID_FILEVIEW),
                    new CurrentLocationOverlay(map),
                    new GridDynOverlay(map, getElevationService()),
                    new NavigationBarOverlay(map),
                    new InformationBarOverlay(map),
                    new EditorOverlay(map, getCacheService(),  GpxInformation.ID.INFO_ID_EDITOR_DRAFT, 
                            getEditorService().getDraftEditor(), getElevationService()),

            };
            map.setOverlayList(overlayList);


            map.frameBoundingBox(getDirectoryService().
                    getCurrent().getBoundingBox());


            DescriptionInterface[] target = new DescriptionInterface[] {
                    multiView, this, busyIndicator
            };

            ContentSource[] source = new ContentSource[] {
                    new EditorSource(getEditorService(),GpxInformation.ID.INFO_ID_EDITOR_DRAFT),
                    new TrackerSource(getTrackerService()),
                    new CurrentLocationSource(getTrackerService()),
                    new OverlaySource((OverlayService)getService(OverlayService.class)),
                    new CurrentFileSource(getDirectoryService())
            };

            setDispatcher(new ContentDispatcher(this,source, target));

        } catch (ServiceNotUpException e) {
            AppLog.e(this, e);
        }
    }



    @Override
    public void onClick(View v) {
        try {
            if (v ==nextView) {
                multiView.setNext();

            } else {
                if (v == previousFile) {
                    getDirectoryService().toPrevious();

                } else if (v ==nextFile) {
                    getDirectoryService().toNext();
                }
                map.frameBoundingBox(getDirectoryService().
                        getCurrent().getBoundingBox());
                getDispatcher().forceUpdate();

            }
        } catch (ServiceNotUpException e) {
            AppLog.e(this, e);
        }
    }

}
