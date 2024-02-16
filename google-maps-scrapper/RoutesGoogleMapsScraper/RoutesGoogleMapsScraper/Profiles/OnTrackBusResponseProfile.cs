using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusResponseProfile : Profile
    {
        public OnTrackBusResponseProfile()
        {
            CreateMap<OnTrackBusResponse, OnTrackBusResponseReadDTO>()
                .ForMember(dest => dest.Channels, opts => opts.MapFrom(src => src.Channels))
                .ForMember(dest => dest.SupportQuestions, opts => opts.MapFrom(src => src.SupportQuestions))
                .ForMember(dest => dest.Routes, opts => opts.MapFrom(src => src.Routes))
                .ForMember(dest => dest.Users, opts => opts.MapFrom(src => src.Users));

            CreateMap<OnTrackBusResponseReadDTO, OnTrackBusResponse>()
                .ForMember(dest => dest.Channels, opts => opts.MapFrom(src => src.Channels))
                .ForMember(dest => dest.SupportQuestions, opts => opts.MapFrom(src => src.SupportQuestions))
                .ForMember(dest => dest.Routes, opts => opts.MapFrom(src => src.Routes))
                .ForMember(dest => dest.Users, opts => opts.MapFrom(src => src.Users));
        }
    }
}
