namespace RoutesGoogleMapsScraper.DTOs
{
    public class OnTrackBusChannelReadDTO
    {
        public string Name;
        public Dictionary<string, OnTrackBusReportReadDTO> Reports;
        public Dictionary<string, OnTrackBusSuggestionReadDTO> Suggestions;
        public float Price;
    }
}
