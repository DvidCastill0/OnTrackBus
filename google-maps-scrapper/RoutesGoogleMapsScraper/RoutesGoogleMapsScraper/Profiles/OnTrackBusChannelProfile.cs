using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusChannelProfile : Profile
    {
        public OnTrackBusChannelProfile()
        {
            CreateMap<OnTrackBusChannel, OnTrackBusChannelReadDTO>()
                .ForMember(dest => dest.Name, opts => opts.MapFrom(src => src.Name))
                .ForMember(dest => dest.Reports, opts => opts.MapFrom(src => src.Reports))
                .ForMember(dest => dest.Suggestions, opts => opts.MapFrom(src => src.Suggestions))
                .ForMember(dest => dest.Price, opts => opts.MapFrom(src => src.Price));

            CreateMap<OnTrackBusChannelReadDTO, OnTrackBusChannel>()
                .ForMember(dest => dest.Name, opts => opts.MapFrom(src => src.Name))
                .ForMember(dest => dest.Reports, opts => opts.MapFrom(src => src.Reports))
                .ForMember(dest => dest.Suggestions, opts => opts.MapFrom(src => src.Suggestions))
                .ForMember(dest => dest.Price, opts => opts.MapFrom(src => src.Price));
        }
    }
}
