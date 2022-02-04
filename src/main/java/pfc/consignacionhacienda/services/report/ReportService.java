package pfc.consignacionhacienda.services.report;

import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.reports.dto.Report;

public interface ReportService {
    Report getReportByAuctionId(Integer auctionId) throws AuctionNotFoundException;
}
