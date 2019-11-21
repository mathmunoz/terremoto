package cl.avenegasm.terremoto.terremotoapi.service.impl;

import cl.avenegasm.terremoto.terremotoapi.dto.EarthquakeResponseDto;
import cl.avenegasm.terremoto.terremotoapi.exception.ExternalApiException;
import cl.avenegasm.terremoto.terremotoapi.service.IEarthquakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Alejandro Venegas
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class EarthquakeServiceImpl implements IEarthquakeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EarthquakeServiceImpl.class);
    private static final String API_ERROR = "Ocurrio un problema al consultar listado de sismos";
    private static final String date_pattern = "yyyy-MM-dd";

    @Value("${terremoto.url}")
    private String endpointEarthquakeApi;

    /**
     * Implementacion del servicio que consulta listado de sismos dentro de un rango de fechas.
     * @param inicio
     * @param fin
     * @return objeto con listado de sismos.
     */
    @Override
    public EarthquakeResponseDto getByRangoFecha(Date inicio, Date fin) {
        String desde = new SimpleDateFormat(date_pattern).format(inicio);
        String hasta = new SimpleDateFormat(date_pattern).format(fin);
        LOGGER.info("Consultando listado de sismos por rango de fechas :{},{}",desde,hasta);
        final String uri = endpointEarthquakeApi +"&starttime="+desde+"&endtime="+hasta;
        EarthquakeResponseDto result = callApiEarthquake(uri);
        if(result.getFeatures() == null || result.getFeatures().isEmpty()){
            LOGGER.warn("Consulta exitosa, pero no se han encontrado registros");
        }else {
            LOGGER.info("Consulta exitosa, se han encontrado :{} sismos", result.getFeatures().size());
        }
        return result;
    }

    @Override
    public EarthquakeResponseDto getByMagnitudRange(Double inicio, Double fin) {
        return null;
    }

    @Override
    public EarthquakeResponseDto getByRangoFecha(Date inicio1, Date fin1, Date inicio2, Date fin2) {
        return null;
    }

    private EarthquakeResponseDto callApiEarthquake(String url){
        try {
            LOGGER.debug("Consumiendo API , url :{}",url);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(url, EarthquakeResponseDto.class);
        }catch (RestClientException ex){
            LOGGER.error(API_ERROR,ex);
            throw new ExternalApiException(API_ERROR);
        }
    }
}