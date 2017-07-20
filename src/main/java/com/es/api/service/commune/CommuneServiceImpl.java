/**
 * 
 */
package com.es.api.service.commune;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.es.api.entities.Commune;
import com.google.gson.Gson;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author odonn
 *
 */
@Component
public class CommuneServiceImpl implements CommuneService {

    private Logger logger = Logger.getLogger(getClass());
    
    @Value("${cluster.name}")
    private String clusterName;
    
    @Value("${cluster.address}")
    private String clusterAddress;

    @Value("${cluster.protocol}")
    private String clusterProtocol;
    
    @Value("${data.input.communes}")
    private String inputPath;
    
    /*
     * (non-Javadoc)
     * 
     * @see test.elastic.search.service.commune.CommuneService#insertCommunes(java.
     * lang.String)
     */
    @Override
    public void insertCommunes() {

	if (StringUtils.isNotEmpty(inputPath)) {

	    final File inputCommune = new File(inputPath);

	    try (FileReader reader = new FileReader(inputCommune);
		    BufferedReader br = new BufferedReader(reader);
		    CSVReader csvReader = new CSVReader(br, ';', '"')) {

		csvReader.readNext();
		String[] csvLine = null;
		Commune commune = null;
		boolean isValidLatLng = Boolean.FALSE;
		Gson gson = new Gson();
		String lat = "";
		String lng = "";
		int index = 1;

		logger.info("Starting processing");

		while ((csvLine = csvReader.readNext()) != null) {

		    isValidLatLng = StringUtils.isNotBlank(csvLine[5]);

		    lat = (isValidLatLng ? StringUtils.trim(csvLine[5].split(",")[0]) : "");
		    lng = (isValidLatLng ? StringUtils.trim(csvLine[5].split(",")[1]) : "");

		    commune = new Commune(csvLine[0], csvLine[1], csvLine[2], csvLine[3], csvLine[4], lat, lng);
		    
		    RestClient restClient = RestClient
			    .builder(new HttpHost(clusterAddress, 9200, clusterProtocol), new HttpHost(clusterAddress, 9201, clusterProtocol))
			    .build();
		    
		    HttpEntity entity = new NStringEntity(gson.toJson(commune), ContentType.APPLICATION_JSON);

		    restClient.performRequest("PUT", "/communes/commune/" + index,
			    Collections.<String, String>emptyMap(), entity);

		    restClient.close();
		    
		    index++;
		}
	    } catch (FileNotFoundException e) {

		logger.error(e.getMessage());
	    } catch (IOException e) {

		logger.error(e.getMessage());
	    }

	    logger.info("Finished process");

	}
    }

    @Override
    public void init() throws IOException {

	final Settings settings = Settings.builder().put("cluster.name", clusterName).build();

	try (final TransportClient client = new PreBuiltTransportClient(settings)) {

	    client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(clusterAddress), 9300));
	    client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(clusterAddress), 9300));

	    final XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("commune")
		    .startObject("properties");

	    builder.startObject("codeINSEE").field("type", "text").startObject("fields").startObject("keyword")
		    .field("type", "keyword").field("ignore_above", "256").endObject().endObject().endObject();

	    builder.startObject("name").field("type", "text").startObject("fields").startObject("keyword")
		    .field("type", "keyword").field("ignore_above", "256").endObject().endObject().endObject();

	    builder.startObject("codePostal").field("type", "text").startObject("fields").startObject("keyword")
		    .field("type", "keyword").field("ignore_above", "256").endObject().endObject().endObject();

	    builder.startObject("libelleAcheminement").field("type", "text").startObject("fields")
		    .startObject("keyword").field("type", "keyword").field("ignore_above", "256").endObject()
		    .endObject().endObject();

	    builder.startObject("line5").field("type", "text").startObject("fields").startObject("keyword")
		    .field("type", "keyword").field("ignore_above", "256").endObject().endObject().endObject();

	    builder.startObject("lat").field("type", "text").startObject("fields").startObject("keyword")
		    .field("type", "keyword").field("ignore_above", "256").endObject().endObject().endObject();

	    builder.startObject("lng").field("type", "text").startObject("fields").startObject("keyword")
		    .field("type", "keyword").field("ignore_above", "256").endObject().endObject().endObject();

	    builder.startObject("location").field("type", "geo_point").endObject();

	    builder.endObject().endObject().endObject();

	    client.admin().indices().prepareCreate("communes").addMapping("commune", builder.string()).get();

	}
    }
}
