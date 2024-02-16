using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusRouteProfile : Profile
    {
        public OnTrackBusRouteProfile()
        {
            CreateMap<OnTrackBusRoute, OnTrackBusRouteReadDTO>()
                .ForMember(dest => dest.Name, opts => opts.MapFrom(src => src.Name))
                .ForMember(dest => dest.Outgoing, opts => opts.MapFrom(src => src.Outgoing))
                .ForMember(dest => dest.Incoming, opts => opts.MapFrom(src => src.Incoming));

            CreateMap<OnTrackBusRouteReadDTO, OnTrackBusRoute>()
                .ForMember(dest => dest.Name, opts => opts.MapFrom(src => src.Name))
                .ForMember(dest => dest.Outgoing, opts => opts.MapFrom(src => src.Outgoing))
                .ForMember(dest => dest.Incoming, opts => opts.MapFrom(src => src.Incoming));
        }
    }
}
