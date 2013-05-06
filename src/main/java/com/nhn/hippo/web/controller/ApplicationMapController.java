package com.nhn.hippo.web.controller;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhn.hippo.web.applicationmap.ApplicationMap;
import com.nhn.hippo.web.calltree.server.ServerCallTree;
import com.nhn.hippo.web.service.ApplicationMapService;
import com.nhn.hippo.web.service.FlowChartService;
import com.nhn.hippo.web.vo.TraceId;

/**
 * 
 * @author netspider
 */
@Controller
public class ApplicationMapController extends BaseController {

	@Autowired
	private ApplicationMapService applicationMapService;

	@Autowired
	private FlowChartService flow;

	@RequestMapping(value = "/getServerMapData2", method = RequestMethod.GET)
	public String getServerMapData2(Model model, HttpServletResponse response, @RequestParam("application") String applicationName, @RequestParam("serviceType") short serviceType, @RequestParam("from") long from, @RequestParam("to") long to) {
		ApplicationMap map = applicationMapService.selectApplicationMap(applicationName, serviceType, from, to);

		model.addAttribute("nodes", map.getNodes());
		model.addAttribute("links", map.getLinks());

		addResponseHeader(response);
		return "applicationmap";
	}

	@RequestMapping(value = "/getLastServerMapData2", method = RequestMethod.GET)
	public String getLastServerMapData2(Model model, HttpServletResponse response, @RequestParam("application") String applicationName, @RequestParam("serviceType") short serviceType, @RequestParam("period") long period) {
		long to = getQueryEndTime();
		long from = to - period;
		return getServerMapData2(model, response, applicationName, serviceType, from, to);
	}
	
	@RequestMapping(value = "/getFilteredServerMapData", method = RequestMethod.GET)
	public String getFilteredServerMapData(Model model, HttpServletResponse response, @RequestParam("application") String applicationName, @RequestParam("serviceType") short serviceType, @RequestParam("from") long from, @RequestParam("to") long to) {
		Set<TraceId> traceIdSet = flow.selectTraceIdsFromApplicationTraceIndex(applicationName, from, to);
		
		System.out.println(traceIdSet);
		
		ServerCallTree map = flow.selectServerCallTree(traceIdSet);
		
		model.addAttribute("nodes", map.getNodes());
		model.addAttribute("links", map.getLinks());

		addResponseHeader(response);
		return "applicationmap.filtered";
	}
}