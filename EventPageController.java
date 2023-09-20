package cs211.project.controllers.event;

import animatefx.animation.FadeIn;
import animatefx.animation.SlideInUp;
import cs211.project.models.event.Event;
import cs211.project.models.event.EventList;
import cs211.project.services.EventListFileDatasource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class EventPageController {
    @FXML private BorderPane root;
    @FXML private GridPane grid;
    @FXML private ScrollPane scrollpane;
    @FXML private TextField searchField;
    private EventList events;
    private EventListFileDatasource datasource;

    @FXML
    private void initialize() throws IOException {
        datasource = new EventListFileDatasource("data/event", "event-list.csv");
        events = datasource.readData();
        root.setLeft(FXMLLoader.load(getClass().getResource("/cs211/project/views/sidebar.fxml")));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                EventList filteredEvents = filterEvents(newValue);
                showGrid(filteredEvents);
            } else {
                showGrid(events);
            }
        });

        showGrid(events);

        FadeIn fadeIn = new FadeIn(root);
        fadeIn.setSpeed(3.0);
        fadeIn.play();
    }

    private EventList filterEvents(String query) {
        EventList filteredEvents = new EventList();

        for (Event filteredEvent : events.getEvents()) {
            if (filteredEvent.getEventName().toLowerCase().contains(query.toLowerCase())) {
                filteredEvents.addEvent(filteredEvent);
            }
        }

        return filteredEvents;
    }

    private void showGrid(EventList events) {
        grid.getChildren().clear();

        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollpane.setPannable(true);

        int columns = 0;
        int rows = 1;

        for (Event event : events.getEvents()) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/cs211/project/views/events/event-card.fxml"));
            VBox eventCard = null;
            try {
                eventCard = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            EventCardController eventCardController = fxmlLoader.getController();
            eventCardController.setData(event);

            if (columns == 3) {
                columns = 0;
                ++rows;
            }

            grid.add(eventCard, columns++, rows);
            GridPane.setMargin(eventCard, new Insets(10));
        }
        new SlideInUp(grid).play();
    }
}