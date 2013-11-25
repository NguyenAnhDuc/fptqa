package vn.com.fpt.fti.api.helper;



import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;


import com.fpt.qa.lucene.analysis.IConstants;
import com.fpt.qa.lucene.analysis.Keyword;
import com.fpt.qa.lucene.analysis.KeywordSelector;
import com.fpt.qa.lucene.analysis.QuestionWordKeywordSelector;
import com.fpt.qa.lucene.analysis.SimpleKeywordSelector;
import com.fpt.qa.platform.reranker.AbstractPassageReranker;
import com.fpt.qa.platform.reranker.DefaultKeywordExpander;
import com.fpt.qa.platform.reranker.KeywordExpander;
import com.fpt.qa.platform.reranker.LinearPassageReranker;

public class RankerHelper {
	private KeywordSelector keywordSelector;
	private KeywordExpander keywordExpander;
	private AbstractPassageReranker passageReranker;
	public RankerHelper(String keywordSelectorFile,String keywordExpanderFile){
		keywordSelector = new SimpleKeywordSelector();
		keywordSelector = new QuestionWordKeywordSelector(keywordSelectorFile); 
		keywordExpander = new DefaultKeywordExpander(keywordExpanderFile);
		passageReranker = new LinearPassageReranker();	
	}
	
	public Query buildQuery(String question){
		Query query = null;
		String[] tokens = question.split(IConstants.SEPARATOR_EXPRESSION);
		List<Keyword> keywords = keywordSelector.select(tokens);
		List<Keyword> expandedKeywords = null;
		boolean useKeywordExpansion = false;
		if (useKeywordExpansion){
			expandedKeywords = keywordExpander.expand(keywords);			
		}
		else {
			expandedKeywords = keywords;
		}
		QueryParser queryParser = new QueryParser(Version.LUCENE_43,IConstants.CONTENT_FIELD_NAME, IConstants.SYNONYM_ANALYZER);
		try{
			StringBuilder queryStr = new StringBuilder();
			for (Keyword kw : expandedKeywords){
				queryStr.append("+"+kw.getTerm() + " ");
			}
			query = queryParser.parse(queryStr.toString().trim());
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		System.out.println(query);
		return query;
	}

	public String getBestDoc(ArrayList<String> docs,Query query){
		

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		List<Document> docss = new ArrayList<Document>();
		// doc1
		Document doc = new Document();
		for (int i = 0; i < docs.size(); i++){
			doc = new Document();
			doc.add(new TextField(IConstants.CONTENT_FIELD_NAME, docs.get(i), Field.Store.YES));
			docss.add(doc);
		}

		Document docresult = passageReranker.rank(docss, query);
		return docresult.get("content");
	}
		
}