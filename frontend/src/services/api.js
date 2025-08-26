import axios from 'axios';

// =====================================================
// Simple Shop Frontend - API 서비스
// =====================================================
// 이 파일은 Backend API와의 모든 HTTP 통신을 담당합니다.
// Axios를 사용하여 RESTful API 호출을 처리합니다.

// API 기본 설정
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000, // 10초 타임아웃
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 (요청 전 처리)
apiClient.interceptors.request.use(
  (config) => {
    console.log(`🚀 API 요청: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('❌ API 요청 에러:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터 (응답 후 처리)
apiClient.interceptors.response.use(
  (response) => {
    console.log(`✅ API 응답: ${response.status} ${response.config.url}`);
    return response;
  },
  (error) => {
    console.error('❌ API 응답 에러:', error.response?.status, error.message);
    return Promise.reject(error);
  }
);

// =====================================================
// 상품 관련 API 함수들
// =====================================================

/**
 * 전체 상품 목록 조회
 * @returns {Promise<Array>} 상품 목록
 */
export const getProducts = async () => {
  try {
    const response = await apiClient.get('/api/products');
    return response.data;
  } catch (error) {
    console.error('상품 목록 조회 실패:', error);
    throw new Error('상품 목록을 불러오는데 실패했습니다.');
  }
};

/**
 * 특정 상품 조회
 * @param {number} id - 상품 ID
 * @returns {Promise<Object>} 상품 정보
 */
export const getProduct = async (id) => {
  try {
    const response = await apiClient.get(`/api/products/${id}`);
    return response.data;
  } catch (error) {
    console.error('상품 조회 실패:', error);
    throw new Error('상품 정보를 불러오는데 실패했습니다.');
  }
};

/**
 * 새 상품 생성
 * @param {Object} product - 생성할 상품 정보
 * @param {string} product.name - 상품명
 * @param {string} product.description - 상품 설명
 * @param {number} product.price - 상품 가격
 * @returns {Promise<Object>} 생성된 상품 정보
 */
export const createProduct = async (product) => {
  try {
    const response = await apiClient.post('/api/products', product);
    return response.data;
  } catch (error) {
    console.error('상품 생성 실패:', error);
    if (error.response?.data) {
      throw new Error(error.response.data);
    }
    throw new Error('상품 생성에 실패했습니다.');
  }
};

/**
 * 상품 정보 수정
 * @param {number} id - 수정할 상품 ID
 * @param {Object} product - 수정할 상품 정보
 * @returns {Promise<Object>} 수정된 상품 정보
 */
export const updateProduct = async (id, product) => {
  try {
    const response = await apiClient.put(`/api/products/${id}`, product);
    return response.data;
  } catch (error) {
    console.error('상품 수정 실패:', error);
    if (error.response?.data) {
      throw new Error(error.response.data);
    }
    throw new Error('상품 수정에 실패했습니다.');
  }
};

/**
 * 상품 삭제
 * @param {number} id - 삭제할 상품 ID
 * @returns {Promise<void>}
 */
export const deleteProduct = async (id) => {
  try {
    await apiClient.delete(`/api/products/${id}`);
  } catch (error) {
    console.error('상품 삭제 실패:', error);
    throw new Error('상품 삭제에 실패했습니다.');
  }
};

/**
 * 상품명으로 검색
 * @param {string} name - 검색할 상품명
 * @returns {Promise<Array>} 검색 결과
 */
export const searchProductsByName = async (name) => {
  try {
    const response = await apiClient.get('/api/products/search/name', {
      params: { name }
    });
    return response.data;
  } catch (error) {
    console.error('상품명 검색 실패:', error);
    throw new Error('상품 검색에 실패했습니다.');
  }
};

/**
 * 상품 설명으로 검색
 * @param {string} description - 검색할 설명
 * @returns {Promise<Array>} 검색 결과
 */
export const searchProductsByDescription = async (description) => {
  try {
    const response = await apiClient.get('/api/products/search/description', {
      params: { description }
    });
    return response.data;
  } catch (error) {
    console.error('상품 설명 검색 실패:', error);
    throw new Error('상품 검색에 실패했습니다.');
  }
};

/**
 * 가격 범위로 검색
 * @param {number} minPrice - 최소 가격
 * @param {number} maxPrice - 최대 가격
 * @returns {Promise<Array>} 검색 결과
 */
export const searchProductsByPriceRange = async (minPrice, maxPrice) => {
  try {
    const response = await apiClient.get('/api/products/search/price', {
      params: { minPrice, maxPrice }
    });
    return response.data;
  } catch (error) {
    console.error('가격 범위 검색 실패:', error);
    throw new Error('상품 검색에 실패했습니다.');
  }
};

/**
 * 키워드로 통합 검색
 * @param {string} keyword - 검색 키워드
 * @returns {Promise<Array>} 검색 결과
 */
export const searchProducts = async (keyword) => {
  try {
    const response = await apiClient.get('/api/products/search', {
      params: { keyword }
    });
    return response.data;
  } catch (error) {
    console.error('통합 검색 실패:', error);
    throw new Error('상품 검색에 실패했습니다.');
  }
};

/**
 * 전체 상품 수 조회
 * @returns {Promise<number>} 상품 수
 */
export const getProductCount = async () => {
  try {
    const response = await apiClient.get('/api/products/count');
    return response.data;
  } catch (error) {
    console.error('상품 수 조회 실패:', error);
    throw new Error('상품 수를 불러오는데 실패했습니다.');
  }
};

/**
 * 평균 가격 이상인 상품 조회
 * @returns {Promise<Array>} 상품 목록
 */
export const getProductsAboveAveragePrice = async () => {
  try {
    const response = await apiClient.get('/api/products/above-average');
    return response.data;
  } catch (error) {
    console.error('평균 가격 이상 상품 조회 실패:', error);
    throw new Error('상품 목록을 불러오는데 실패했습니다.');
  }
};

// =====================================================
// 에러 처리 유틸리티
// =====================================================

/**
 * API 에러를 사용자 친화적인 메시지로 변환
 * @param {Error} error - 에러 객체
 * @returns {string} 사용자 친화적인 에러 메시지
 */
export const getErrorMessage = (error) => {
  if (error.response) {
    // 서버 응답이 있는 경우
    const status = error.response.status;
    switch (status) {
      case 400:
        return '잘못된 요청입니다. 입력값을 확인해주세요.';
      case 401:
        return '인증이 필요합니다.';
      case 403:
        return '접근 권한이 없습니다.';
      case 404:
        return '요청한 리소스를 찾을 수 없습니다.';
      case 500:
        return '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
      default:
        return error.response.data || '알 수 없는 오류가 발생했습니다.';
    }
  } else if (error.request) {
    // 요청은 보냈지만 응답을 받지 못한 경우
    return '서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.';
  } else {
    // 요청 자체를 보내지 못한 경우
    return error.message || '알 수 없는 오류가 발생했습니다.';
  }
};

// API 클라이언트 내보내기 (직접 사용이 필요한 경우)
export default apiClient;
