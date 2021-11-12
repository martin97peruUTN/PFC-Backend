package pfc.consignacionhacienda.services.locality;

import javassist.tools.web.BadHttpRequest;
import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.exceptions.InternalServerException;
import pfc.consignacionhacienda.exceptions.locality.LocalityNotFoundException;
import pfc.consignacionhacienda.model.Locality;

import java.util.List;

public interface LocalityService {
    Locality getLocalityById(Integer id) throws LocalityNotFoundException;
    
    List<Locality> getAllAvailablesLocalities();

    Page<Locality> getAllLocalitiesByPages(Integer pageNumber, Integer limit);
    List<Locality> getAllLocalities();
    Locality saveLocality(Locality locality) throws BadHttpRequest, pfc.consignacionhacienda.exceptions.BadHttpRequest;
    Locality updateLocalityById(Integer id, Locality locality) throws LocalityNotFoundException, pfc.consignacionhacienda.exceptions.BadHttpRequest;
    Locality deleteLocalityById(Integer id) throws InternalServerException, LocalityNotFoundException;
}
