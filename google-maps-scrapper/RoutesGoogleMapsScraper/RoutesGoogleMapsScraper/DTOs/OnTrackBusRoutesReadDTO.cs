using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusRoutesReadDTO
    {
        public List<OnTrackBusRouteReadDTO> Routes { get; set; }

        public OnTrackBusRoutesReadDTO()
        {
            Routes = new List<OnTrackBusRouteReadDTO>();
        }
    }
}
