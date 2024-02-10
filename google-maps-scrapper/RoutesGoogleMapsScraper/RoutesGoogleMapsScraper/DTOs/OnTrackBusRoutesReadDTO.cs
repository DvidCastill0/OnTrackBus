using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusRoutesReadDTO
    {
        public List<OnTrackBusRouteReadDTO> Routes;

        public OnTrackBusRoutesReadDTO()
        {
            Routes = new List<OnTrackBusRouteReadDTO>();
        }
    }
}
