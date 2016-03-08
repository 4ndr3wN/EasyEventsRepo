package tue.easyevents;

/**
 * Created by s137092 on 8-3-2016.
 */
public class Event {

    String addressEvent;
    String infoEvent;
    String pictureEvent;
    String ticketEvent;
    String titleEvent;
    String venueEvent;

    double eventLongtitude;
    double eventLatitude;
    double ratingEvent;
    double ratingUsers;
    double ratingVenue;
    double ratingWeather;

    int dateEvent;
    int idEvent;

    public Event(String address, String info, String picture, String ticket, String title, String venue,
                 double longtitude, double latitude, double ratingEvent, double ratingUsers, double ratingVenue,
                 double ratingWeather, int date, int id) {

        this.addressEvent = address; //get from api
        this.infoEvent = info;
        this.pictureEvent = picture;
        this.ticketEvent = ticket;
        this.titleEvent = title;
        this.venueEvent = venue;

        this.eventLatitude = longtitude;
        this.eventLongtitude = latitude;
        this.ratingEvent = ratingEvent;
        this.ratingUsers = ratingUsers;
        this.ratingWeather = ratingWeather;
        this.ratingVenue = ratingVenue;

        this.dateEvent = date;
        this.idEvent = id;
    }
}
