using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusRoutesReadDTO
    {
        public List<OnTrackBusRoute> Routes;

        public OnTrackBusRoutesReadDTO()
        {
            Routes = new List<OnTrackBusRoute>();
        }
    }
}
