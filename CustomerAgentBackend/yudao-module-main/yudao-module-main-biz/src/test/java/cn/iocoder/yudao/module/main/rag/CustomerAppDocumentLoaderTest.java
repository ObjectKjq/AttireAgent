package cn.iocoder.yudao.module.main.rag;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@Import(CustomerAppDocumentLoader.class)
class CustomerAppDocumentLoaderTest extends BaseMockitoUnitTest {

    @Resource
    private CustomerAppDocumentLoader customerAppDocumentLoader;

    @Test
    void loadMarkdowns() {
        customerAppDocumentLoader.loadMarkdowns();
    }
}