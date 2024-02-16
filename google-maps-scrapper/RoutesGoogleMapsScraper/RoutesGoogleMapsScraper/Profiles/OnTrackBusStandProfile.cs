using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusStandProfile : Profile
    {
        public OnTrackBusStandProfile() 
        {
            CreateMap<OnTrackBusStand, OnTrackBusStandReadDTO>()
                .ForMember(dest => dest.Name, opts => opts.MapFrom(src => src.Name))
                .ForMember(dest => dest.Latitude, opts => opts.MapFrom(src => src.Latitude))
                .ForMember(dest => dest.Longitude, opts => opts.MapFrom(src => src.Longitude))
                .ForMember(dest => dest.Prediction, opts => opts.MapFrom(src => DateTime.ParseExact(src.Prediction, "HH:mm;ss d-MM-yyyy", null)))
                .ForMember(dest => dest.LastTimeBoarded, opts => opts.MapFrom(src => DateTime.ParseExact(src.LastTimeBoarded, "HH:mm:ss  d-MM-yyyy", null)));

            CreateMap<OnTrackBusStandReadDTO, OnTrackBusStand>()
                .ForMember(dest => dest.Name, opts => opts.MapFrom(src => src.Name))
                .ForMember(dest => dest.Latitude, opts => opts.MapFrom(src => src.Latitude))
                .ForMember(dest => dest.Longitude, opts => opts.MapFrom(src => src.Longitude))
                .ForMember(dest => dest.Prediction, opts => opts.MapFrom(src => src.Prediction.ToString("HH:mm;ss d-MM-yyyy")))
                .ForMember(dest => dest.LastTimeBoarded, opts => opts.MapFrom(src => src.LastTimeBoarded.ToString("HH:mm:ss  d-MM-yyyy")));
        }
    }
}
