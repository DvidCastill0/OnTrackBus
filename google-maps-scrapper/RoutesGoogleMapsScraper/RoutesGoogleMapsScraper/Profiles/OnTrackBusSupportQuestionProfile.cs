using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusSupportQuestionProfile : Profile
    {
        public OnTrackBusSupportQuestionProfile()
        {
            CreateMap<OnTrackBusSupportQuestion, OnTrackBusSupportQuestionReadDTO>()
                .ForMember(dest => dest.Content, opts => opts.MapFrom(src => src.Content))
                .ForMember(dest => dest.Email, opts => opts.MapFrom(src => src.Email))
                .ForMember(dest => dest.IssuedDateTime, opts => opts.MapFrom(src => DateTime.ParseExact(src.IssuedDateTime, "HH:mm:ss d-MM-yyyy", null)));

            CreateMap<OnTrackBusSupportQuestionReadDTO, OnTrackBusSupportQuestion>()
                .ForMember(dest => dest.Content, opts => opts.MapFrom(src => src.Content))
                .ForMember(dest => dest.Email, opts => opts.MapFrom(src => src.Email))
                .ForMember(dest => dest.IssuedDateTime, opts => opts.MapFrom(src => src.IssuedDateTime.ToString("HH:mm:ss d-MM-yyyy")));
        }
    }
}
