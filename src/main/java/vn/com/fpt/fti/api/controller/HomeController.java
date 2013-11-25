package vn.com.fpt.fti.api.controller;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.naming.directory.SearchResult;

import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import fpt.qa.qiafc.AFConverter;
import fpt.qa.qiafc.AffirmativeForm;
import vn.com.fpt.fti.api.helper.RankerHelper;
import vn.com.fpt.fti.api.helper.ResponseHelper;
import vn.com.fpt.fti.api.model.AffirmativeResponse;
import vn.com.fpt.fti.api.model.FailResponseJson;
import vn.com.fpt.fti.api.model.Person;
import vn.com.fpt.fti.api.model.RankerRequest;
import vn.com.fpt.fti.api.model.SolrResponse;
import vn.com.fpt.fti.api.model.SuccessResponseJson;
import vn.hus.nlp.tokenizer.VietTokenizer;
 


@Controller
@RequestMapping("/")
public class HomeController {
	@Value("${expirationOffset}") public int exprnOffset;
	public VietTokenizer vietTokenizer;
	public AbstractSequenceClassifier classifier;
	
	@Value("${keywordSelectorFile}") String keywordSelectorFile;
	@Value("${keywordExpanderFile}") String keywordExpanderFile;
	@Value("${vietTokenPropertyFile}") String vietTokenFile;
	@Value("${dataPath}") String dataPath;
	@Value("${model}") String model;
	
	@PostConstruct
	public void init(){
		
		System.out.println(this.vietTokenFile);
		this.vietTokenizer = new VietTokenizer(this.vietTokenFile);
		
		try{
			this.classifier = CRFClassifier.getClassifier(model);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		System.out.println("HomeController Init");
	}
	
	@RequestMapping(value="/index", method = RequestMethod.GET)
	public String index(ModelMap model) {
		model.addAttribute("message", "Maven Web Project + Spring 3 MVC - welcome()");
		return "index";
 	}

	@RequestMapping(value="/test", method = RequestMethod.GET)
	@ResponseBody
	public String testFunction(){
		String result = "This is result of a test function";
		return ""+ this.vietTokenFile;
	}
	
	@RequestMapping(value="/testArray", method = RequestMethod.POST, produces="text/html; charset=UTF-8")
	@ResponseBody
	public String testArray(@RequestParam("sText") SolrResponse request){
		String result = "This is result of a test Array function";
		//return request.status;
		return request.status;
	}
	
	@RequestMapping(value="/testRanker", method = RequestMethod.POST, produces="application/json; charset=UTF-8")
	@ResponseBody
	public SuccessResponseJson testRanker(@RequestBody RankerRequest rankerRequest){
		String result = "This is result of a test Array function";
		//return request.status;
		RankerHelper rankerHelper = new RankerHelper(keywordSelectorFile, keywordExpanderFile);
		Query query = rankerHelper.buildQuery(rankerRequest.searchContent);
		result = rankerHelper.getBestDoc(rankerRequest.answers, query);
		return ResponseHelper.buildSuccessJsonReponse(result);
	}
	
	@RequestMapping(value="/testJSON", method = RequestMethod.POST, produces="application/json; charset=UTF-8")
	@ResponseBody
	public Person testArray2(){
		String result = "This is result of a test Array function";
		//return request.status;
		Person person = new Person();
		person.name = "mit";
		person.age = 21;
		return person;
	}
	
	@RequestMapping(value="/testParam/{id}", method = RequestMethod.POST, produces="text/html; charset=UTF-8")
	@ResponseBody
	public String testParam(@PathVariable String id){
		String result = "This is result of a test function";
		return id;
	}
	
	
	
	/*@RequestMapping(value="/testRanker", method = RequestMethod.GET, produces="text/html; charset=UTF-8")
	@ResponseBody
	public String testRanker(){
		String result = "This is result of a test function";
		String searchcontent = "Chủ tịch fpt là ai";
		ArrayList<String> ans = new ArrayList<String>();
		ans.add("Ông Trương Gia Bình là chủ tịch tập đoàn fpt");
		ans.add("Nguyễn Anh Đức là cựu sinh viên fpt");
		RankerHelper rankerHelper = new RankerHelper(keywordSelectorFile, keywordExpanderFile);
		Query query = rankerHelper.buildQuery(searchcontent);
		result = rankerHelper.getBestDoc(ans, query);
		
		return result;
	}*/
	
	@RequestMapping(value="/testToken", method = RequestMethod.POST, produces="application/json; charset=UTF-8")
	@ResponseBody
	public SuccessResponseJson testToken(@RequestParam("sText") String sInput){
		String result = "";
		result = this.vietTokenizer.tokenize(sInput)[0];
		System.out.println("Success Token");
		return ResponseHelper.buildSuccessJsonReponse(result);
	}
	
	
	@RequestMapping(value="/testAffirmative", method = RequestMethod.POST, produces="application/json; charset=UTF-8")
	@ResponseBody
	public AffirmativeResponse testAffirmative(@RequestParam("sText") String sQuestion){
		AffirmativeResponse  affirmativeResponse = new AffirmativeResponse();
		try{
			AffirmativeForm affirm = AFConverter.process(sQuestion, this.dataPath);
			affirmativeResponse.status = "success";
			affirmativeResponse.questionWord = affirm.getQuestionWord();
			affirmativeResponse.entityType = affirm.getNamedEntityType();
			return affirmativeResponse;
		}
		catch (Exception ex){
			affirmativeResponse.status = "fail"; // sau nay phai improve
			return affirmativeResponse;
		}
	}
	
	@RequestMapping(value="/testClassify", method = RequestMethod.POST, produces="application/json; charset=UTF-8")
	@ResponseBody
	public SuccessResponseJson testClassify(@RequestParam("sText") String sText){
		String result = "";
		result = this.classifier.classifyWithInlineXML(sText);
		return ResponseHelper.buildSuccessJsonReponse(result);
	}

}