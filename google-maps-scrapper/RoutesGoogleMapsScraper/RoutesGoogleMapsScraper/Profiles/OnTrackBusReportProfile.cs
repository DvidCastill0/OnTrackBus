using AutoMapper;
using RoutesGoogleMapsScraper.DTOs;
using RoutesGoogleMapsScraper.Models;

namespace RoutesGoogleMapsScraper.Profiles
{
    public class OnTrackBusReportProfile : Profile
    {
        public OnTrackBusReportProfile()
        {
            CreateMap<OnTrackBusReport, OnTrackBusReportReadDTO>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.IssuedDateTime, opts => opts.MapFrom(src => DateTime.ParseExact(src.IssuedDateTime, "HH:mm:ss d-MM-yyyy", null)))
                .ForMember(dest => dest.ChannelReportDateTime, opts => opts.MapFrom(src => DateTime.ParseExact(src.ChannelReportDateTime, "HH:mm:ss d-MM-yyyy", null)))
                .ForMember(dest => dest.Email, opts => opts.MapFrom(src => src.Email))
                .ForMember(dest => dest.ChannelReportId, opts => opts.MapFrom(src => src.ChannelReportId))
                .ForMember(dest => dest.Location, opts => opts.MapFrom(src => src.Location))
                .ForMember(dest => dest.UnitNumber, opts => opts.MapFrom(src => src.UnitNumber));

            CreateMap<OnTrackBusReportReadDTO, OnTrackBusReport>()
                .ForMember(dest => dest.Id, opts => opts.MapFrom(src => src.Id))
                .ForMember(dest => dest.IssuedDateTime, opts => opts.MapFrom(src => src.IssuedDateTime.ToString("HH:mm:ss d-MM-yyyy")))
                .ForMember(dest => dest.ChannelReportDateTime, opts => opts.MapFrom(src => src.ChannelReportDateTime.ToString("HH:mm:ss d-MM-yyyy", null)))
                .ForMember(dest => dest.Email, opts => opts.MapFrom(src => src.Email))
                .ForMember(dest => dest.ChannelReportId, opts => opts.MapFrom(src => src.ChannelReportId))
                .ForMember(dest => dest.Location, opts => opts.MapFrom(src => src.Location))
                .ForMember(dest => dest.UnitNumber, opts => opts.MapFrom(src => src.UnitNumber));
        }
    }
}
