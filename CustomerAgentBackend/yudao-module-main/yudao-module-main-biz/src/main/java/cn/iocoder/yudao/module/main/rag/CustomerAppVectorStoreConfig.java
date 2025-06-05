package cn.iocoder.yudao.module.main.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CustomerAppVectorStoreConfig {

    @Resource
    private CustomerAppDocumentLoader customerAppDocumentLoader;
    @Resource
    private MyKeyWordEnricher myKeyWordEnricher;

    @Bean
    VectorStore customerAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        // 基于内存的向量存储
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        List<Document> documents = customerAppDocumentLoader.loadMarkdowns();
        documents = myKeyWordEnricher.enrichDocuments(documents);
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }

}
