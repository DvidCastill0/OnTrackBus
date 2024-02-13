using AutoMapper;
using Newtonsoft.Json;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Repositories;
using RoutesGoogleMapsScraper.Server;
using Serilog;

namespace RoutesGoogleMapsScraper.Controllers
{
    public static class OnTrackBusController
    {
        public static string hostname;

        public static Response ProcessRequest(ILogger logger, IMapper mapper, Request request)
        {
            return request.Endpoint.ToLower() switch
            {
                "" => GetAll(logger, mapper, request),
                "allchannels" => GetAllChannels(logger, mapper, request),
                "allreports" => GetAllReports(logger, mapper, request),
                "allroutes" => GetAllRoutes(logger, mapper, request),
                "allstands" => GetAllStands(logger, mapper, request),
                "allsuggestions" => GetAllSuggestions(logger, mapper, request),
                "allsupportquestions" => GetAllSupportQuestions(logger, mapper, request),
                "allusers" => GetAllUsers(logger, mapper, request),
                "channelbyname" => GetChannelByName(logger, mapper, request),
                "channelbynumber" => GetChannelByNumber(logger, mapper, request),
                "channelsbyprice" => GetChannelsByPrice(logger, mapper, request),
                "reportbyid" => GetReportById(logger, mapper, request),
                "reportsbychannelreportid" => GetReportsByChannelReportId(logger, mapper, request),
                "reportsbylocation" => GetReportsByLocation(logger, mapper, request),
                "routebyid" => GetRouteById(logger, mapper, request),
                "routebyname" => GetRouteByName(logger, mapper, request),
                "selectedchannelsbyuserid" => GetSelectedChannelsByUserId(logger, mapper, request),
                "outgoingstandsbyid" => GetOutgoingStandsById(logger, mapper, request),
                "incomingstandsbyid" => GetIncomingStandsById(logger, mapper, request),
                "standsbyid" => GetStandsById(logger, mapper, request),
                "outgoingstandsbyname" => GetOutgoingStandsByName(logger, mapper, request),
                "incomingstandsbyname" => GetIncomingStandsByName(logger, mapper, request),
                "standsbyname" => GetStandsByName(logger, mapper, request),
                "suggestionbyid" => GetSuggestionById(logger, mapper, request),
                "suggestionsbyemail" => GetSuggestionsByEmail(logger, mapper, request),
                "supportquestionbyid" => GetSupportQuestionById(logger, mapper, request),
                "supportquestionsbyemail" => GetSupportQuestionsByEmail(logger, mapper, request),
                "userbyid" => GetUserById(logger, mapper, request),
                "activitylogbyuserid" => GetActivityLogByUserId(logger, mapper, request),
                "activityreportsbyuseridandroute" => GetActivityReportsByUserIdAndRoute(logger, mapper, request),
                "activityreportsbyuseridandstand" => GetActivityReportsByUserIdAndStand(logger, mapper, request),
                _ => new Response(JsonConvert.SerializeObject(new ResponseMessage(false, "Invalid endpoint on OnTrackBus controller")), 404)
            };
        }

        public static Response GetAll(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var repository = new OnTrackBusRepository(logger, hostname);

            var items = mapper.Map<OnTrackBusResponseReadDTO>(repository.GetAll());
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusResponseReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Channels: {items.Channels.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Routes: {items.Routes.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Users: {items.Users.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Support Questions: {items.SupportQuestions.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetAllChannels(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var repository = new OnTrackBusRepository(logger, hostname);

            var items = mapper.Map<Dictionary<string, OnTrackBusChannelReadDTO>>(repository.GetAllChannels());

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No channels found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "Not channels found")), 200);
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusChannelReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Channels: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetAllReports(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var repository = new OnTrackBusRepository(logger, hostname);

            var items = mapper.Map<Dictionary<string, OnTrackBusReportReadDTO>>(repository.GetAllReports());

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No reports found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "No reports found")), 200);
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusReportReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Reports: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetAllRoutes(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var repository = new OnTrackBusRepository(logger, hostname);

            var items = mapper.Map<Dictionary<string, OnTrackBusRouteReadDTO>>(repository.GetAllRoutes());

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No routes found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "No routes found")), 200);
            }


            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusRouteReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Routes: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetAllStands(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var repository = new OnTrackBusRepository(logger, hostname);

            var items = mapper.Map<Dictionary<string, OnTrackBusStandReadDTO>>(repository.GetAllStands());

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No stands found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "No stands found")), 200);
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusStandReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stands: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetAllSuggestions(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var repository = new OnTrackBusRepository(logger, hostname);

            var items = mapper.Map<Dictionary<string, OnTrackBusSuggestionReadDTO>>(repository.GetAllSuggestions());

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No suggestions found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "No suggestions found")), 200);
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusSuggestionReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Suggestions: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetAllSupportQuestions(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var repository = new OnTrackBusRepository(logger, hostname);

            var items = mapper.Map<Dictionary<string, OnTrackBusSupportQuestionReadDTO>>(repository.GetAllSupportQuestions());

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No support questions found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "No support questions found found")), 200);
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusSupportQuestionReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Support Questions: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetAllUsers(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var repository = new OnTrackBusRepository(logger, hostname);

            var items = mapper.Map<Dictionary<string, OnTrackBusUserReadDTO>>(repository.GetAllUsers());

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No users found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "No users found")), 200);
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusUserReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Users: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetChannelByName(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusChannelReadDTO channel;

            try
            {
                channel = JsonConvert.DeserializeObject<OnTrackBusChannelReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusChannel format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (channel == null || string.IsNullOrWhiteSpace(channel.Name))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusChannel format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var channelFound = repository.GetChannelByName(channel.Name);

            if (channelFound == null)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Channel \"{channel.Name}\" not found");
                return ErrorController.getNotFound(logger, $"Channel \"{channel.Name}\" not found");
            }

            var item = mapper.Map<OnTrackBusChannelReadDTO>(channelFound);
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusChannelReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Channel found: {item.Name}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Reports: {item.Reports.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Suggestions: {item.Suggestions.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(item), 200);
        }

        public static Response GetChannelByNumber(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusChannelReadDTO channel;

            try
            {
                channel = JsonConvert.DeserializeObject<OnTrackBusChannelReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusChannel format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (channel == null || channel.Id <= 0)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusChannel format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var channelFound = repository.GetChannelByNumber(channel.Id);

            if (channelFound == null)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Channel with Id \"{channel.Name}\" not found");
                return ErrorController.getNotFound(logger, $"Channel \"{channel.Name}\" not found");
            }

            var item = mapper.Map<OnTrackBusChannelReadDTO>(channelFound);
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusChannelReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Channel found: {item.Name}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Reports: {item.Reports.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Suggestions: {item.Suggestions.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(item), 200);
        }

        public static Response GetChannelsByPrice(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusChannelReadDTO channel;

            try
            {
                channel = JsonConvert.DeserializeObject<OnTrackBusChannelReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusChannel format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (channel == null || channel.Price < 0)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusChannel format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusChannelReadDTO>>(repository.GetChannelsByPrice(channel.Price));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No channels found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "No channels found")), 200);
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusChannelReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Channels: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetReportById(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusReportReadDTO report;

            try
            {
                report = JsonConvert.DeserializeObject<OnTrackBusReportReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (report == null || string.IsNullOrWhiteSpace(report.Id))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var reportFound = repository.GetReportById(report.Id);

            if (reportFound == null)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Report with Id \"{report.Id}\" not found");
                return ErrorController.getNotFound(logger, $"Report with Id \"{report.Id}\" not found");
            }

            var item = mapper.Map<OnTrackBusReportReadDTO>(reportFound);
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusReportReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Report found: {item.Id}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(item), 200);
        }

        public static Response GetReportsByChannelReportId(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusReportReadDTO report;

            try
            {
                report = JsonConvert.DeserializeObject<OnTrackBusReportReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (report == null || string.IsNullOrWhiteSpace(report.ChannelReportId))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusReportReadDTO>>(repository.GetReportsByChannelReportId(report.ChannelReportId));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No reports found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "No reports found")), 200);
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusReportReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Reports: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetReportsByLocation(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusReportReadDTO report;

            try
            {
                report = JsonConvert.DeserializeObject<OnTrackBusReportReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (report == null || string.IsNullOrWhiteSpace(report.Location))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusReportReadDTO>>(repository.GetReportsByLocation(report.Location));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No reports found");
                return new Response(JsonConvert.SerializeObject(new ResponseMessage(true, "No reports found")), 200);
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusReportReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Reports: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetRouteById(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusRouteReadDTO route;

            try
            {
                route = JsonConvert.DeserializeObject<OnTrackBusRouteReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusRoute format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (route == null || route.Id <= 0)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusRoute format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var routeFound = repository.GetRouteById(route.Id);

            if (routeFound == null)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Route with Id \"{route.Id}\" not found");
                return ErrorController.getNotFound(logger, $"Route with Id \"{route.Id}\" not found");
            }

            var item = mapper.Map<OnTrackBusRouteReadDTO>(routeFound);
            item.Id = route.Id;
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusRouteReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Route found: {item.Id}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Name: {item.Name}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Outgoing stands: {item.Outgoing.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Incoming stands: {item.Incoming.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(item), 200);
        }

        public static Response GetRouteByName(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusRouteReadDTO route;

            try
            {
                route = JsonConvert.DeserializeObject<OnTrackBusRouteReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusRoute format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (route == null || string.IsNullOrWhiteSpace(route.Name))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusRoute format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var routeFound = repository.GetRouteByName(route.Name);

            if (routeFound == null)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Route \"{route.Name}\" not found");
                return ErrorController.getNotFound(logger, $"Route \"{route.Name}\" not found");
            }

            var item = mapper.Map<OnTrackBusRouteReadDTO>(routeFound);
            item.Id = route.Id;
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusRouteReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Route found: {item.Name}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Name: {item.Name}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Outgoing stands: {item.Outgoing.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Incoming stands: {item.Incoming.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(item), 200);
        }

        public static Response GetSelectedChannelsByUserId(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusUserReadDTO user;

            try
            {
                user = JsonConvert.DeserializeObject<OnTrackBusUserReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusUser format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (user == null || string.IsNullOrWhiteSpace(user.Id))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusUser format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusSelectedChannelReadDTO>>(repository.GetSelectedChannelsByUserId(user.Id));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for user \"{user.Id}\"");
                return ErrorController.getNotFound(logger, $"Not records found for user \"{user.Id}\"");
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusSelectedChannelReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Selected channels: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetOutgoingStandsById(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusStandReadDTO stand;

            try
            {
                stand = JsonConvert.DeserializeObject<OnTrackBusStandReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (stand == null || stand.Id <= 0)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusStandReadDTO>>(repository.GetOutgoingStandsById(stand.Id));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for stand \"{stand.Id}\"");
                return ErrorController.getNotFound(logger, $"Not records found for stand \"{stand.Id}\"");
            }

            foreach (var item in items)
                item.Value.Id = stand.Id;

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusStandReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stand found: {stand.Id}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stands: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetIncomingStandsById(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusStandReadDTO stand;

            try
            {
                stand = JsonConvert.DeserializeObject<OnTrackBusStandReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (stand == null || stand.Id <= 0)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusStandReadDTO>>(repository.GetIncomingStandsById(stand.Id));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for stand \"{stand.Id}\"");
                return ErrorController.getNotFound(logger, $"Not records found for stand \"{stand.Id}\"");
            }

            foreach (var item in items)
                item.Value.Id = stand.Id;

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusStandReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stand found: {stand.Id}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stands: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetStandsById(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusStandReadDTO stand;

            try
            {
                stand = JsonConvert.DeserializeObject<OnTrackBusStandReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (stand == null || stand.Id <= 0)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusStandReadDTO>>(repository.GetStandsById(stand.Id));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for stand \"{stand.Id}\"");
                return ErrorController.getNotFound(logger, $"Not records found for stand \"{stand.Id}\"");
            }

            foreach (var item in items)
                item.Value.Id = stand.Id;

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusStandReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stand found: {stand.Id}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stands: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetOutgoingStandsByName(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusStandReadDTO stand;

            try
            {
                stand = JsonConvert.DeserializeObject<OnTrackBusStandReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (stand == null || string.IsNullOrWhiteSpace(stand.Name))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusStandReadDTO>>(repository.GetOutgoingStandsByName(stand.Name));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for stand \"{stand.Name}\"");
                return ErrorController.getNotFound(logger, $"Not records found for stand \"{stand.Name}\"");
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusStandReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stand found: {stand.Name}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stands: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetIncomingStandsByName(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusStandReadDTO stand;

            try
            {
                stand = JsonConvert.DeserializeObject<OnTrackBusStandReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (stand == null || string.IsNullOrWhiteSpace(stand.Name))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusStandReadDTO>>(repository.GetIncomingStandsByName(stand.Name));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for stand \"{stand.Name}\"");
                return ErrorController.getNotFound(logger, $"Not records found for stand \"{stand.Name}\"");
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusStandReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stand found: {stand.Name}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stands: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetStandsByName(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusStandReadDTO stand;

            try
            {
                stand = JsonConvert.DeserializeObject<OnTrackBusStandReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (stand == null || string.IsNullOrWhiteSpace(stand.Name))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusStand format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusStandReadDTO>>(repository.GetStandsByName(stand.Name));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for stand \"{stand.Name}\"");
                return ErrorController.getNotFound(logger, $"Not records found for stand \"{stand.Name}\"");
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusStandReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stand found: {stand.Name}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Stands: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetSuggestionById(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusSuggestionReadDTO suggestion;

            try
            {
                suggestion = JsonConvert.DeserializeObject<OnTrackBusSuggestionReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusSuggestion format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (suggestion == null || string.IsNullOrWhiteSpace(suggestion.Id))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusSuggestion format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var suggestionFound = repository.GetSuggestionById(suggestion.Id);

            if (suggestionFound == null)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Suggestion with Id \"{suggestion.Id}\" not found");
                return ErrorController.getNotFound(logger, $"Suggestion with Id \"{suggestion.Id}\" not found");
            }

            var item = mapper.Map<OnTrackBusSuggestionReadDTO>(suggestionFound);
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusRouteReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Suggestion found: {item.Id}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(item), 200);
        }

        public static Response GetSuggestionsByEmail(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusSuggestionReadDTO suggestion;

            try
            {
                suggestion = JsonConvert.DeserializeObject<OnTrackBusSuggestionReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusSuggestion format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (suggestion == null || string.IsNullOrWhiteSpace(suggestion.Email))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusSuggestion format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusSuggestionReadDTO>>(repository.GetSuggestionsByEmail(suggestion.Email));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for suggestion with email \"{suggestion.Email}\"");
                return ErrorController.getNotFound(logger, $"Not records found for suggestion with email \"{suggestion.Email}\"");
            }

            foreach (var item in items)
                item.Value.Id = item.Key;

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusStandReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Email found: {suggestion.Email}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Suggestions: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetSupportQuestionById(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusSupportQuestionReadDTO supportquestion;

            try
            {
                supportquestion = JsonConvert.DeserializeObject<OnTrackBusSupportQuestionReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusSupportQuestion format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (supportquestion == null || string.IsNullOrWhiteSpace(supportquestion.Id))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusSupportQuestion format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var supportQuestionFound = repository.GetSupporQuestionById(supportquestion.Id);

            if (supportQuestionFound == null)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Support question \"{supportquestion.Id}\" not found");
                return ErrorController.getNotFound(logger, $"Support question \"{supportquestion.Id}\" not found");
            }

            var item = mapper.Map<OnTrackBusSupportQuestionReadDTO>(supportQuestionFound);
            item.Id = supportquestion.Id;
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusSupportQuestionReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Support question found: {item.Id}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(item), 200);
        }

        public static Response GetSupportQuestionsByEmail(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusSupportQuestionReadDTO supportQuestion;

            try
            {
                supportQuestion = JsonConvert.DeserializeObject<OnTrackBusSupportQuestionReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusSupportQuestion format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (supportQuestion == null || string.IsNullOrWhiteSpace(supportQuestion.Email))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusSupportQuestion format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusSupportQuestionReadDTO>>(repository.GetSupportQuestionsByEmail(supportQuestion.Email));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for support question with email \"{supportQuestion.Email}\"");
                return ErrorController.getNotFound(logger, $"Not records found for support question with email \"{supportQuestion.Email}\"");
            }

            foreach (var item in items)
                item.Value.Id = item.Key;

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusSupportQuestionReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Email found: {supportQuestion.Email}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Support Questions: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetUserById(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusUserReadDTO user;

            try
            {
                user = JsonConvert.DeserializeObject<OnTrackBusUserReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusUser format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (user == null || string.IsNullOrWhiteSpace(user.Id))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusUser format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var userFound = repository.GetUserById(user.Id);

            if (userFound == null)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - User with Id \"{user.Id}\" not found");
                return ErrorController.getNotFound(logger, $"User with Id \"{user.Id}\" not found");
            }

            var item = mapper.Map<OnTrackBusUserReadDTO>(userFound);
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusUserReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - User found: {item.Id}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Fullname: {item.Firstname} {item.Lastname}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Top Routes: {item.TopRoutes.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Selected Channels: {item.SelectedChannels.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(item), 200);
        }

        public static Response GetActivityLogByUserId(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusUserReadDTO user;

            try
            {
                user = JsonConvert.DeserializeObject<OnTrackBusUserReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusUser format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (user == null || string.IsNullOrWhiteSpace(user.Id))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusUser format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, Dictionary<string, OnTrackBusActivityReportReadDTO>>>(repository.GetActivityLogByUserId(user.Id));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - No records found for user with Id \"{user.Id}\"");
                return ErrorController.getNotFound(logger, $"No records found for user with Id \"{user.Id}\"");
            }

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusActivityReportReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - User found: {user.Id}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetActivityReportsByUserIdAndRoute(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusActivityReportReadDTO activityReport;

            try
            {
                activityReport = JsonConvert.DeserializeObject<OnTrackBusActivityReportReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusActivityReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (activityReport == null || string.IsNullOrWhiteSpace(activityReport.UserId) || string.IsNullOrWhiteSpace(activityReport.RouteName))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusActivityReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusActivityReportReadDTO>>(repository.GetActivityReportsByUserIdAndRoute(activityReport.UserId, activityReport.RouteName));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for user with Id \"{activityReport.UserId}\" and route \"{activityReport.RouteName}\"");
                return ErrorController.getNotFound(logger, $"Not records found for user with Id \"{activityReport.UserId}\" and route \"{activityReport.RouteName}\"");
            }

            foreach (var item in items)
                item.Value.UserId = activityReport.UserId;

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusActivityReportReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Found (UserId: {activityReport.UserId}) (Route: {activityReport.RouteName})");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Activity Reports: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }

        public static Response GetActivityReportsByUserIdAndStand(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("OnTrackBusController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }

            var bodyTask = request.GetBody();

            bodyTask.Wait();

            var body = bodyTask.Result;

            OnTrackBusActivityReportReadDTO activityReport;

            try
            {
                activityReport = JsonConvert.DeserializeObject<OnTrackBusActivityReportReadDTO>(body);
            }
            catch (Exception)
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusActivityReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            if (activityReport == null || string.IsNullOrWhiteSpace(activityReport.UserId) || string.IsNullOrWhiteSpace(activityReport.StandName))
            {
                logger.Error($"(GUID: {request.Guid}) - Request #{request.Sequence} - OnTrackBusActivityReport format in request body is not valid");
                return ErrorController.getNotAcceptable(logger, "Not valid request body format");
            }

            var repository = new OnTrackBusRepository(logger, hostname);
            var items = mapper.Map<Dictionary<string, OnTrackBusActivityReportReadDTO>>(repository.GetActivityReportsByUserIdAndStand(activityReport.UserId, activityReport.StandName));

            if (items.Count == 0)
            {
                logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Not records found for user with Id \"{activityReport.UserId}\" and stand \"{activityReport.StandName}\"");
                return ErrorController.getNotFound(logger, $"Not records found for user with Id \"{activityReport.UserId}\" and stand \"{activityReport.StandName}\"");
            }

            foreach (var item in items)
                item.Value.UserId = activityReport.UserId;

            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Converted into OnTrackBusActivityReportReadDTO");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Found (UserId: {activityReport.UserId}) (Stand: {activityReport.StandName})");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Activity Reports: {items.Count}");
            logger.Debug($"(GUID: {request.Guid}) - Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }
    }
}
