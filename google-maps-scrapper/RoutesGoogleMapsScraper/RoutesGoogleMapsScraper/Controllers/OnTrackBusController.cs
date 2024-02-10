using AutoMapper;
using Newtonsoft.Json;
using RoutesGoogleMapsScraper.APIs;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;
using RoutesGoogleMapsScraper.Server;
using Serilog;

namespace RoutesGoogleMapsScraper.Controllers
{
    public static class OnTrackBusController
    {
        public static string hostname;

        public static Response ProcessRequest(ILogger logger, IMapper mapper, Request request)
        { 
            return request.Endpoint switch
            { 
                "" => GetAll(logger, mapper, request),
                _ => new Response(JsonConvert.SerializeObject(new ResponseMessage(false, "Invalid endpoint on OnTrackBus controller")))
            };
        }

        public static Response GetAll(ILogger logger, IMapper mapper, Request request)
        {
            if (request == null)
            {
                logger.Error("LexicalAnalyzerController received an empty request!");
                return ErrorController.getNotAcceptable(logger, "Empty request");
            }


            var onTrackBusAPI = new OnTrackBusAPI(logger, "https", hostname);
            onTrackBusAPI.BuildUri();

            var onTrackBusStringResponse = onTrackBusAPI.GetResource();

            var onTrackBusResponse = JsonConvert.DeserializeObject<OnTrackBusResponse>(onTrackBusStringResponse);

            if (onTrackBusResponse == null)
            {
                logger.Information($"Request #{request.Sequence} - Not records found");
                return new Response("Not data found", 200);
            }

            logger.Debug($"Request #{request.Sequence} - Converting into Read DTO");
            var items = mapper.Map<OnTrackBusResponseReadDTO>(onTrackBusResponse);
            logger.Debug($"Request #{request.Sequence} - Channels: {items.Channels.Count}");
            logger.Debug($"Request #{request.Sequence} - Routes: {items.Routes.Count}");
            logger.Debug($"Request #{request.Sequence} - Users: {items.Users.Count}");
            logger.Debug($"Request #{request.Sequence} - Support Questions: {items.SupportQuestions.Count}");
            logger.Debug($"Request #{request.Sequence} - Sending response back");

            return new Response(JsonConvert.SerializeObject(items), 200);
        }
    }
}
