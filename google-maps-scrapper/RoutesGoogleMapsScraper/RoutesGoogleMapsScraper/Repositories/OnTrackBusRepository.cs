using AutoMapper.Execution;
using Newtonsoft.Json;
using RoutesGoogleMapsScraper.APIs;
using RoutesGoogleMapsScraper.Models;
using Serilog;
using System.Net.WebSockets;
using System.Runtime.InteropServices;
using System.Security.Authentication.ExtendedProtection;
using System.Threading.Channels;

namespace RoutesGoogleMapsScraper.Repositories
{
    public class OnTrackBusRepository : IOnTrackBusRepository
    {
        private ILogger _logger;
        private OnTrackBusAPI _api;

        public OnTrackBusRepository(ILogger logger, string hostname)
        {
            _logger = logger;
            _api = new OnTrackBusAPI(logger, "https", hostname);
            _api.BuildUri();
        }

        private OnTrackBusResponse GetAllData()
        {
            var onTrackBusStringResponse = _api.GetResource();

            var item = JsonConvert.DeserializeObject<OnTrackBusResponse>(onTrackBusStringResponse);

            if (item == null)
            {
                _logger.Warning("No records found on OnTrackBus");
                return new OnTrackBusResponse();
            }

            return item;
        }

        public Dictionary<string, Dictionary<string, OnTrackBusActivityReport>> GetActivityLogByUserId(string id)
        {
            var data = GetAllData();

            var activityLog = new Dictionary<string, Dictionary<string, OnTrackBusActivityReport>>();
            foreach (var user in data.Users)
            {
                if (!user.Key.Equals(id) && !user.Value.Id.Equals(id))
                    continue;

                activityLog = user.Value.ActivityLog;
            }

            return activityLog;
        }

        public Dictionary<string, OnTrackBusActivityReport> GetActivityReportsByUserIdAndRoute(string id, string route)
        {
            var data = GetAllData();

            var activityReports = new Dictionary<string, OnTrackBusActivityReport>();
            foreach (var user in data.Users)
            {
                if (!user.Key.Equals(id) && !user.Value.Id.Equals(id))
                    continue;

                foreach (var activity in user.Value.ActivityLog)
                {
                    foreach (var report in activity.Value)
                    {
                        if (!report.Value.RouteName.ToUpper().Equals(route))
                            continue;

                        activityReports.Add($"{id}-{activity.Key}-{report.Key}", report.Value);
                    }
                }
            }

            return activityReports;
        }

        public Dictionary<string, OnTrackBusActivityReport> GetActivityReportsByUserIdAndStand(string id, string stand)
        {
            var data = GetAllData();

            var activityReports = new Dictionary<string, OnTrackBusActivityReport>();
            foreach (var user in data.Users)
            {
                if (!user.Key.Equals(id) && !user.Value.Id.Equals(id))
                    continue;

                foreach (var activity in user.Value.ActivityLog)
                {
                    foreach (var report in activity.Value)
                    {
                        if (!report.Value.StandName.ToUpper().Contains(stand.ToUpper()))
                            continue;

                        activityReports.Add($"{id}-{activity.Key}-{report.Key}", report.Value);
                    }
                }
            }

            return activityReports;
        }

        public OnTrackBusResponse GetAll()
        {
            return GetAllData();
        }

        public Dictionary<string, OnTrackBusChannel> GetAllChannels()
        {
            return GetAllData().Channels;
        }

        public Dictionary<string, OnTrackBusReport> GetAllReports()
        {
            var data = GetAllData();
            
            var reports = new Dictionary<string, OnTrackBusReport>();
            foreach(var channel in data.Channels)
            {
                foreach (var report in channel.Value.Reports)
                {
                    reports.Add(report.Key, report.Value);
                }
            }

            return reports;
        }

        public Dictionary<string, OnTrackBusRoute> GetAllRoutes()
        {
            return GetAllData().Routes;
        }

        public Dictionary<string, OnTrackBusStand> GetAllStands()
        {
            var data = GetAllData();

            var stands = new Dictionary<string, OnTrackBusStand>();
            foreach (var route in data.Routes)
            {
                foreach (var stand in route.Value.Incoming)
                {
                    stands.Add($"{route.Key}-Incoming-{stand.Key}", stand.Value);
                }

                foreach (var stand in route.Value.Outgoing)
                {
                    stands.Add($"{route.Key}-Outgoing-{stand.Key}", stand.Value);
                }
            }

            return stands;
        }

        public Dictionary<string, OnTrackBusSuggestion> GetAllSuggestions()
        {
            var data = GetAllData();

            var suggestions = new Dictionary<string, OnTrackBusSuggestion>();
            foreach (var channel in data.Channels)
            {
                foreach (var suggestion in channel.Value.Suggestions)
                { 
                    suggestions.Add(suggestion.Key, suggestion.Value);
                }
            }

            return suggestions;
        }

        public Dictionary<string, OnTrackBusSupportQuestion> GetAllSupportQuestions()
        {
            return GetAllData().SupportQuestions;
        }

        public Dictionary<string, OnTrackBusUser> GetAllUsers()
        {
            return GetAllData().Users;
        }

        public OnTrackBusChannel GetChannelByName(string name)
        {
            var data = GetAllData();

            foreach (var channel in data.Channels)
            {
                if (channel.Value.Name.ToLower().Equals(name))
                    return channel.Value;
            }

            return null;
        }

        public OnTrackBusChannel GetChannelByNumber(int number)
        {
            var data = GetAllData();

            foreach (var channel in data.Channels)
            {
                var channelId = channel.Key;

                if (!channelId.ToUpper().Contains("CANAL"))
                    continue;

                var channelNumberString = channelId.Substring(5);
                int channelNumber = -1;

                try
                {
                    channelNumber = Convert.ToInt32(channelNumberString);
                }
                catch (Exception)
                {
                    continue;
                }

                if (channelNumber > 0 && channelNumber == number)
                    return channel.Value;
            }

            return null;
        }

        public Dictionary<string, OnTrackBusChannel> GetChannelsByPrice(float price)
        {
            var data = GetAllData();

            var channels = new Dictionary<string, OnTrackBusChannel>();
            foreach (var channel in data.Channels)
            {
                if (channel.Value.Price != price)
                    continue;

                channels.Add(channel.Key, channel.Value);
            }

            return channels;
        }

        public OnTrackBusReport GetReportById(string id)
        {
            var data = GetAllData();

            foreach (var channel in data.Channels)
            {
                foreach (var report in channel.Value.Reports)
                {
                    if (report.Key.Equals(id) || report.Value.Id.Equals(id))
                        return report.Value;
                }
            }

            return null;
        }

        public Dictionary<string, OnTrackBusReport> GetReportsByChannelReportId(string channelReportId)
        {
            var data = GetAllData();

            var reports = new Dictionary<string, OnTrackBusReport>();

            foreach (var channel in data.Channels)
            {
                foreach (var report in channel.Value.Reports)
                {
                    if (!report.Value.ChannelReportId.ToUpper().Equals(channelReportId.ToUpper()))
                        continue;

                    reports.Add(report.Key, report.Value);
                }
            }

            return reports;
        }

        public Dictionary<string, OnTrackBusReport> GetReportsByLocation(string location)
        {
            var data = GetAllData();

            var reports = new Dictionary<string, OnTrackBusReport>();
            foreach (var channel in data.Channels)
            {
                foreach (var report in channel.Value.Reports)
                {
                    if (!report.Value.Location.ToUpper().Contains(location.ToUpper()))
                        continue;

                    reports.Add(report.Key, report.Value);
                }
            }

            return reports;
        }

        public OnTrackBusRoute GetRouteById(int id)
        {
            var data = GetAllData();

            foreach (var route in data.Routes)
            {
                var routeId = route.Key;

                if (!routeId.ToUpper().Contains("RUTA"))
                    continue;

                var routeNumberString = routeId.Substring(4);
                int routeNumber = -1;

                try
                {
                    routeNumber = Convert.ToInt32(routeNumberString);
                }
                catch (Exception)
                {
                    continue;
                }

                if (routeNumber <= 0 || routeNumber != id)
                    continue;

                return route.Value;
            }

            return null;
        }

        public OnTrackBusRoute GetRouteByName(string name)
        {
            var data = GetAllData();

            foreach (var route in data.Routes)
            {
                if (route.Value.Name.ToUpper().Equals(name.ToUpper()))
                    return route.Value;
            }

            return null;
        }

        public Dictionary<string, OnTrackBusSelectedChannel> GetSelectedChannelsByUserId(string id)
        {
            var data = GetAllData();

            var selectedChannels = new Dictionary<string, OnTrackBusSelectedChannel>();
            foreach (var user in data.Users)
            {
                if (!user.Key.Equals(id) && !user.Value.Id.Equals(id))
                    continue;

                selectedChannels = user.Value.SelectedChannels;
                break;
            }

            return selectedChannels;
        }

        public Dictionary<string, OnTrackBusStand> GetOutgoingStandsById(int id)
        {
            var data = GetAllData();

            var stands = new Dictionary<string, OnTrackBusStand>();
            foreach (var route in data.Routes)
            {
                foreach (var stand in route.Value.Outgoing)
                {
                    var standId = stand.Key;

                    if (!standId.ToUpper().Contains("PARADA"))
                        continue;

                    var standNumberString = standId.Substring(6);
                    int standNumber = -1;

                    try
                    {
                        standNumber = Convert.ToInt32(standNumberString);
                    }
                    catch (Exception)
                    {
                        continue;
                    }

                    if (standNumber <= 0 || standNumber != id)
                        continue;

                    stands.Add(route.Value.Name, stand.Value);
                }
            }

            return stands;
        }

        public Dictionary<string, OnTrackBusStand> GetIncomingStandsById(int id)
        {
            var data = GetAllData();

            var stands = new Dictionary<string, OnTrackBusStand>();
            foreach (var route in data.Routes)
            {
                foreach (var stand in route.Value.Incoming)
                {
                    var standId = stand.Key;

                    if (!standId.ToUpper().Contains("PARADA"))
                        continue;

                    var standNumberString = standId.Substring(6);
                    int standNumber = -1;

                    try
                    {
                        standNumber = Convert.ToInt32(standNumberString);
                    }
                    catch (Exception)
                    {
                        continue;
                    }

                    if (standNumber <= 0 || standNumber != id)
                        continue;

                    stands.Add(route.Value.Name, stand.Value);
                }
            }

            return stands;
        }

        public Dictionary<string, OnTrackBusStand> GetStandsById(int id)
        {
            var data = GetAllData();

            var stands = new Dictionary<string, OnTrackBusStand>();
            foreach (var route in data.Routes)
            {
                foreach (var stand in route.Value.Incoming)
                {
                    var standId = stand.Key;

                    if (!standId.ToUpper().Contains("PARADA"))
                        continue;

                    var standNumberString = standId.Substring(6);
                    int standNumber = -1;

                    try
                    {
                        standNumber = Convert.ToInt32(standNumberString);
                    }
                    catch (Exception)
                    {
                        continue;
                    }

                    if (standNumber <= 0 || standNumber != id)
                        continue;

                    stands.Add($"{route.Value.Name}-Outgoing", stand.Value);
                }

                foreach (var stand in route.Value.Incoming)
                {
                    var standId = stand.Key;

                    if (!standId.ToUpper().Contains("PARADA"))
                        continue;

                    var standNumberString = standId.Substring(6);
                    int standNumber = -1;

                    try
                    {
                        standNumber = Convert.ToInt32(standNumberString);
                    }
                    catch (Exception)
                    {
                        continue;
                    }

                    if (standNumber <= 0 || standNumber != id)
                        continue;

                    stands.Add($"{route.Value.Name}-Incoming", stand.Value);
                }
            }

            return stands;
        }

        public Dictionary<string, OnTrackBusStand> GetOutgoingStandsByName(string name)
        {
            var data = GetAllData();

            var stands = new Dictionary<string, OnTrackBusStand>();
            int cont = 1;
            cont++;
            foreach (var route in data.Routes)
            {
                foreach (var stand in route.Value.Outgoing)
                {
                    if (!stand.Value.Name.ToUpper().Contains(name.ToUpper()))
                        continue;

                    stands.Add($"{route.Value.Name}-S{cont}", stand.Value);
                    cont++;
                }
            }

            return stands;
        }

        public Dictionary<string, OnTrackBusStand> GetIncomingStandsByName(string name)
        {
            var data = GetAllData();

            var stands = new Dictionary<string, OnTrackBusStand>();
            int cont = 1;

            foreach (var route in data.Routes)
            {
                foreach (var stand in route.Value.Incoming)
                {
                    if (!stand.Value.Name.ToUpper().Contains(name.ToUpper()))
                        continue;

                    stands.Add($"{route.Value.Name}-S{cont}", stand.Value);
                    cont++;
                }
            }

            return stands;
        }

        public Dictionary<string, OnTrackBusStand> GetStandsByName(string name)
        {
            var data = GetAllData();

            var stands = new Dictionary<string, OnTrackBusStand>();
            int cont = 1;

            foreach (var route in data.Routes)
            {
                foreach (var stand in route.Value.Outgoing)
                {
                    if (!stand.Value.Name.ToUpper().Contains(name.ToUpper()))
                        continue;

                    stands.Add($"{route.Value.Name}-Outgoing-S{cont}", stand.Value);
                    cont++;
                }

                foreach (var stand in route.Value.Incoming)
                {
                    if (!stand.Value.Name.ToUpper().Contains(name.ToUpper()))
                        continue;

                    stands.Add($"{route.Value.Name}-Incoming-S{cont}", stand.Value);
                    cont++;
                }
            }

            return stands;
        }

        public OnTrackBusSuggestion GetSuggestionById(string id)
        {
            var data = GetAllData();

            foreach (var channel in data.Channels)
            {
                foreach (var suggestion in channel.Value.Suggestions)
                {
                    if (!suggestion.Key.Equals(id) && !suggestion.Value.Id.Equals(id))
                        continue;

                    return suggestion.Value;
                }
            }

            return null;
        }

        public Dictionary<string, OnTrackBusSuggestion> GetSuggestionsByEmail(string email)
        {
            var data = GetAllData();

            var suggestions = new Dictionary<string, OnTrackBusSuggestion>();
            foreach (var channel in data.Channels)
            {
                foreach (var suggestion in channel.Value.Suggestions)
                {
                    if (!suggestion.Value.Email.ToUpper().Equals(email.ToUpper()))
                        continue;

                    suggestions.Add(suggestion.Key, suggestion.Value);
                }
            }

            return suggestions;
        }

        public OnTrackBusSupportQuestion GetSupporQuestionById(string id)
        {
            var data = GetAllData();

            foreach (var supportquestion in data.SupportQuestions)
            {
                if (!supportquestion.Key.Equals(id))
                    continue;

                return supportquestion.Value;
            }

            return null;
        }

        public Dictionary<string, OnTrackBusSupportQuestion> GetSupportQuestionsByEmail(string email)
        {
            var data = GetAllData();

            var supportQuestions = new Dictionary<string, OnTrackBusSupportQuestion>();
            foreach (var supportQuestion in data.SupportQuestions)
            {
                if (!supportQuestion.Value.Email.ToUpper().Equals(email.ToUpper()))
                    continue;

                supportQuestions.Add(supportQuestion.Key, supportQuestion.Value);
            }

            return supportQuestions;
        }

        public OnTrackBusUser GetUserById(string id)
        {
            var data = GetAllData();

            foreach (var user in data.Users)
            {
                if (!user.Key.Equals(id) && !user.Value.Id.Equals(id))
                    continue;

                return user.Value;
            }

            return null;
        }
    }
}
