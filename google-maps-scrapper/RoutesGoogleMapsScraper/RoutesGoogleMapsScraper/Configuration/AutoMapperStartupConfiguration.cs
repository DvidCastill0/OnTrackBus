using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Configuration
{
    public class AutoMapperStartupConfiguration
    {
        private MapperConfiguration _config;

        public AutoMapperStartupConfiguration()
        {
            _config = new MapperConfiguration(config =>
            {
                config.CreateMap<OnTrackBusResponse, OnTrackBusRoutesReadDTO>()
                .ForMember(dest => dest.Routes, opts => opts.MapFrom(src => src.Routes));
            });
        }

        public MapperConfiguration Configuration 
        {
            get => _config;
        }
    }
}
