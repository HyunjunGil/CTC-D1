import React, { useState, useEffect } from 'react';
import { getProducts, deleteProduct, searchProducts } from '../services/api';
import './ProductList.css';

// =====================================================
// ProductList 컴포넌트
// =====================================================
// 이 컴포넌트는 상품 목록을 표시하고, 상품 삭제 기능을 제공합니다.
// 검색 기능도 포함되어 있어 사용자가 원하는 상품을 쉽게 찾을 수 있습니다.

const ProductList = ({ onEditProduct, onRefresh }) => {
  // 상태 관리
  const [products, setProducts] = useState([]); // 상품 목록
  const [loading, setLoading] = useState(true); // 로딩 상태
  const [error, setError] = useState(null); // 에러 상태
  const [searchKeyword, setSearchKeyword] = useState(''); // 검색 키워드
  const [filteredProducts, setFilteredProducts] = useState([]); // 필터링된 상품 목록
  const [deleteConfirm, setDeleteConfirm] = useState(null); // 삭제 확인 상태

  // 컴포넌트 마운트 시 상품 목록 로드
  useEffect(() => {
    loadProducts();
  }, []);

  // 검색 키워드 변경 시 필터링
  useEffect(() => {
    if (searchKeyword.trim() === '') {
      setFilteredProducts(products);
    } else {
      const filtered = products.filter(product =>
        product.name.toLowerCase().includes(searchKeyword.toLowerCase()) ||
        product.description.toLowerCase().includes(searchKeyword.toLowerCase())
      );
      setFilteredProducts(filtered);
    }
  }, [searchKeyword, products]);

  // 상품 목록 로드 함수
  const loadProducts = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getProducts();
      setProducts(data);
      setFilteredProducts(data);
    } catch (error) {
      setError('상품 목록을 불러오는데 실패했습니다.');
      console.error('상품 목록 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  // 상품 삭제 함수
  const handleDelete = async (productId, productName) => {
    try {
      await deleteProduct(productId);
      
      // 로컬 상태에서 삭제된 상품 제거
      setProducts(prevProducts => 
        prevProducts.filter(product => product.id !== productId)
      );
      
      // 삭제 확인 상태 초기화
      setDeleteConfirm(null);
      
      // 부모 컴포넌트에 새로고침 알림
      if (onRefresh) {
        onRefresh();
      }
      
      // 성공 메시지 표시 (실제로는 Toast나 알림 컴포넌트 사용 권장)
      alert(`${productName}이(가) 삭제되었습니다.`);
      
    } catch (error) {
      setError('상품 삭제에 실패했습니다.');
      console.error('상품 삭제 실패:', error);
    }
  };

  // 검색 처리 함수
  const handleSearch = (e) => {
    e.preventDefault();
    if (searchKeyword.trim() === '') {
      loadProducts(); // 검색어가 없으면 전체 목록 로드
    }
  };

  // 검색어 초기화 함수
  const clearSearch = () => {
    setSearchKeyword('');
    loadProducts();
  };

  // 가격 포맷팅 함수
  const formatPrice = (price) => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW'
    }).format(price);
  };

  // 날짜 포맷팅 함수
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  // 로딩 중일 때 표시
  if (loading) {
    return (
      <div className="product-list-container">
        <div className="spinner"></div>
        <p>상품 목록을 불러오는 중...</p>
      </div>
    );
  }

  // 에러가 있을 때 표시
  if (error) {
    return (
      <div className="product-list-container">
        <div className="alert alert-danger">
          <strong>오류 발생:</strong> {error}
          <button 
            className="btn btn-primary" 
            onClick={loadProducts}
            style={{ marginLeft: '10px' }}
          >
            다시 시도
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="product-list-container">
      {/* 헤더 섹션 */}
      <div className="product-list-header">
        <h2>📦 상품 목록</h2>
        <p>총 {filteredProducts.length}개의 상품이 있습니다.</p>
      </div>

      {/* 검색 섹션 */}
      <div className="search-section">
        <form onSubmit={handleSearch} className="search-form">
          <div className="search-input-group">
            <input
              type="text"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              placeholder="상품명이나 설명으로 검색..."
              className="search-input"
            />
            <button type="submit" className="btn btn-primary">
              🔍 검색
            </button>
            {searchKeyword && (
              <button 
                type="button" 
                onClick={clearSearch}
                className="btn btn-warning"
              >
                ✖️ 초기화
              </button>
            )}
          </div>
        </form>
      </div>

      {/* 상품 목록 */}
      {filteredProducts.length === 0 ? (
        <div className="no-products">
          <p>📭 {searchKeyword ? '검색 결과가 없습니다.' : '등록된 상품이 없습니다.'}</p>
          {searchKeyword && (
            <button 
              onClick={clearSearch}
              className="btn btn-primary"
            >
              전체 상품 보기
            </button>
          )}
        </div>
      ) : (
        <div className="products-grid">
          {filteredProducts.map((product) => (
            <div key={product.id} className="product-card card fade-in">
              <div className="product-header">
                <h3 className="product-name">{product.name}</h3>
                <span className="product-price">{formatPrice(product.price)}</span>
              </div>
              
              <div className="product-description">
                {product.description || '설명이 없습니다.'}
              </div>
              
              <div className="product-meta">
                <small className="product-date">
                  📅 {formatDate(product.createdAt)}
                </small>
                {product.updatedAt !== product.createdAt && (
                  <small className="product-updated">
                    ✏️ {formatDate(product.updatedAt)}
                  </small>
                )}
              </div>
              
              <div className="product-actions">
                <button
                  onClick={() => onEditProduct(product)}
                  className="btn btn-warning"
                >
                  ✏️ 수정
                </button>
                
                <button
                  onClick={() => setDeleteConfirm(product.id)}
                  className="btn btn-danger"
                >
                  🗑️ 삭제
                </button>
              </div>

              {/* 삭제 확인 모달 */}
              {deleteConfirm === product.id && (
                <div className="delete-confirm-modal">
                  <div className="delete-confirm-content">
                    <h4>⚠️ 삭제 확인</h4>
                    <p>
                      <strong>"{product.name}"</strong> 상품을 정말 삭제하시겠습니까?
                    </p>
                    <p className="delete-warning">
                      이 작업은 되돌릴 수 없습니다.
                    </p>
                    <div className="delete-confirm-actions">
                      <button
                        onClick={() => handleDelete(product.id, product.name)}
                        className="btn btn-danger"
                      >
                        ✅ 삭제
                      </button>
                      <button
                        onClick={() => setDeleteConfirm(null)}
                        className="btn btn-warning"
                      >
                        ❌ 취소
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* 새로고침 버튼 */}
      <div className="refresh-section">
        <button 
          onClick={loadProducts}
          className="btn btn-primary"
        >
          🔄 새로고침
        </button>
      </div>
    </div>
  );
};

export default ProductList;
