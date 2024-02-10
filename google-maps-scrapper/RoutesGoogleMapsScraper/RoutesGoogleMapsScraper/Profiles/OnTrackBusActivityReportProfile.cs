using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusActivityReportProfile : Profile
    {
        public OnTrackBusActivityReportProfile()
        {
            CreateMap<OnTrackBusActivityReport, OnTrackBusActivityReportReadDTO>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.BoardingDateTime, opts => opts.MapFrom(src => DateTime.ParseExact(src.BoardingTimeOnly + src.BoardingDateOnly, "HH:mm:ss d-MM-yyyy", null)))
                .ForMember(dest => dest.RouteName, opts => opts.MapFrom(src => src.RouteName))
                .ForMember(dest => dest.StandName, opts => opts.MapFrom(src => src.StandName));

            CreateMap<OnTrackBusActivityReportReadDTO, OnTrackBusActivityReport>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.BoardingDateOnly, opts => opts.MapFrom(src => src.BoardingDateTime.ToString("d-MM-yyyy")))
                .ForMember(dest => dest.BoardingTimeOnly, opts => opts.MapFrom(src => src.BoardingDateTime.ToString("HH:mm:ss")))
                .ForMember(dest => dest.BoardingDateTime, opts => opts.MapFrom(src => src.BoardingDateTime))
                .ForMember(dest => dest.RouteName, opts => opts.MapFrom(src => src.RouteName))
                .ForMember(dest => dest.StandName, opts => opts.MapFrom(src => src.StandName));
        }
    }
}
