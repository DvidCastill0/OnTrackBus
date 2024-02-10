using AutoMapper;
using RoutesGoogleMapsScraper.Profiles;

namespace RoutesGoogleMapsScraper.Configuration
{
    public class AutoMapperStartupConfiguration
    {
        private MapperConfiguration _config;

        public AutoMapperStartupConfiguration()
        {
            _config = new MapperConfiguration(config =>
            {
                config.AddProfile<OnTrackBusActivityReportProfile>();
                config.AddProfile<OnTrackBusChannelProfile>();
                config.AddProfile<OnTrackBusReportProfile>();
                config.AddProfile<OnTrackBusRouteProfile>();
                config.AddProfile<OnTrackBusSelectedChannelProfile>();
                config.AddProfile<OnTrackBusStandProfile>();
                config.AddProfile<OnTrackBusSuggestionProfile>();
                config.AddProfile<OnTrackBusSupportQuestionProfile>();
                config.AddProfile<OnTrackBusUserProfile>();
                config.AddProfile<OnTrackBusResponseProfile>();
            });
        }

        public MapperConfiguration Configuration 
        {
            get => _config;
        }
    }
}
