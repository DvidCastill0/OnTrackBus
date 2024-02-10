namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusResponseReadDTO
    {
        public Dictionary<string, OnTrackBusChannelReadDTO> Channels { get; set; }
        
        public Dictionary<string, OnTrackBusSupportQuestionReadDTO> SupportQuestions { get; set; }
        
        public Dictionary<string, OnTrackBusRouteReadDTO> Routes { get; set; }
        public Dictionary<string, OnTrackBusUserReadDTO> Users { get; set; }
    }
}
