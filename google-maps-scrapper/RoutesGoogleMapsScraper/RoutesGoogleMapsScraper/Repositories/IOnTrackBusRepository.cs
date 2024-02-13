using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Repositories
{
    public interface IOnTrackBusRepository
    {
        public OnTrackBusResponse GetAll();
        public Dictionary<string, OnTrackBusChannel> GetAllChannels();
        public OnTrackBusChannel GetChannelByNumber(int number);
        public OnTrackBusChannel GetChannelByName(string name);
        public Dictionary<string, OnTrackBusChannel> GetChannelsByPrice(float price);
        public Dictionary<string, OnTrackBusReport> GetAllReports();
        public OnTrackBusReport GetReportById(string id);
        public Dictionary<string, OnTrackBusReport> GetReportsByChannelReportId(string channelReportId);
        public Dictionary<string, OnTrackBusReport> GetReportsByLocation(string location);
        public Dictionary<string, OnTrackBusSuggestion> GetAllSuggestions();
        public OnTrackBusSuggestion GetSuggestionById(string id);
        public Dictionary<string, OnTrackBusSuggestion> GetSuggestionsByEmail(string email);
        public Dictionary<string, OnTrackBusSupportQuestion> GetAllSupportQuestions();
        public OnTrackBusSupportQuestion GetSupporQuestionById(string id);
        public Dictionary<string, OnTrackBusSupportQuestion> GetSupportQuestionsByEmail(string email);
        public Dictionary<string, OnTrackBusRoute> GetAllRoutes();
        public OnTrackBusRoute GetRouteById(int id);
        public OnTrackBusRoute GetRouteByName(string name);
        public Dictionary<string, OnTrackBusStand> GetAllStands();
        public Dictionary<string, OnTrackBusStand> GetOutgoingStandsById(int id);
        public Dictionary<string, OnTrackBusStand> GetIncomingStandsById(int id);
        public Dictionary<string, OnTrackBusStand> GetStandsById(int id);
        public Dictionary<string, OnTrackBusStand> GetOutgoingStandsByName(string name);
        public Dictionary<string, OnTrackBusStand> GetIncomingStandsByName(string name);
        public Dictionary<string, OnTrackBusStand> GetStandsByName(string name);
        public Dictionary<string, OnTrackBusUser> GetAllUsers();
        public OnTrackBusUser GetUserById(string id);
        public Dictionary<string, OnTrackBusSelectedChannel> GetSelectedChannelsByUserId(string id);
        public Dictionary<string, Dictionary<string, OnTrackBusActivityReport>> GetActivityLogByUserId(string id);
        public Dictionary<string, OnTrackBusActivityReport> GetActivityReportsByUserIdAndRoute(string id, string route);
        public Dictionary<string, OnTrackBusActivityReport> GetActivityReportsByUserIdAndStand(string id, string stand);

    }
}
