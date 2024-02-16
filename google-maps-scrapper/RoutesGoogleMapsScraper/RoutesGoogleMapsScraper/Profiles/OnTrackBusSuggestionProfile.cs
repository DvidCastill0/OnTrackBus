using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusSuggestionProfile : Profile
    {
        public OnTrackBusSuggestionProfile()
        {
            CreateMap<OnTrackBusSuggestion, OnTrackBusSuggestionReadDTO>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.Email, opts => opts.MapFrom(src => src.Email))
                .ForMember(dest => dest.IssuedDateTime, opts => opts.MapFrom(src => DateTime.ParseExact(src.IssuedDateTime, "HH:mm:ss d-MM-yyyy", null)))
                .ForMember(dest => dest.Content, opts => opts.MapFrom(src => src.Content));

            CreateMap<OnTrackBusSuggestionReadDTO, OnTrackBusSuggestion>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.Email, opts => opts.MapFrom(src => src.Email))
                .ForMember(dest => dest.IssuedDateTime, opts => opts.MapFrom(src => src.IssuedDateTime.ToString("HH:mm:ss d-MM-yyyy")))
                .ForMember(dest => dest.Content, opts => opts.MapFrom(src => src.Content));
        }
    }
}
