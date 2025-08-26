import React, { useState } from 'react';
import ProductList from './components/ProductList';
import ProductForm from './components/ProductForm';
import './App.css';

// =====================================================
// Simple Shop - 메인 App 컴포넌트
// =====================================================
// 이 컴포넌트는 전체 애플리케이션의 메인 컨테이너 역할을 합니다.
// ProductList와 ProductForm을 관리하고, 상품 데이터의 상태를 조정합니다.

function App() {
  // 애플리케이션 상태 관리
  const [currentView, setCurrentView] = useState('list'); // 'list' 또는 'form'
  const [editingProduct, setEditingProduct] = useState(null); // 수정 중인 상품
  const [refreshTrigger, setRefreshTrigger] = useState(0); // 목록 새로고침 트리거

  // =====================================================
  // 이벤트 핸들러 함수들
  // =====================================================

  /**
   * 새 상품 추가 모드로 전환
   */
  const handleAddNew = () => {
    setEditingProduct(null);
    setCurrentView('form');
  };

  /**
   * 상품 수정 모드로 전환
   * @param {Object} product - 수정할 상품 정보
   */
  const handleEditProduct = (product) => {
    setEditingProduct(product);
    setCurrentView('form');
  };

  /**
   * 폼 취소 처리
   */
  const handleCancelForm = () => {
    setCurrentView('list');
    setEditingProduct(null);
  };

  /**
   * 상품 저장 완료 처리
   * @param {Object} savedProduct - 저장된 상품 정보
   */
  const handleProductSaved = (savedProduct) => {
    // 목록 새로고침 트리거 증가
    setRefreshTrigger(prev => prev + 1);
    
    // 목록 화면으로 돌아가기
    setTimeout(() => {
      setCurrentView('list');
      setEditingProduct(null);
    }, 1000); // 1초 후 자동으로 목록 화면으로 이동
  };

  /**
   * 목록 새로고침 처리
   */
  const handleRefresh = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  // =====================================================
  // 렌더링 로직
  // =====================================================

  // 현재 뷰에 따라 적절한 컴포넌트 렌더링
  const renderCurrentView = () => {
    switch (currentView) {
      case 'form':
        return (
          <ProductForm
            product={editingProduct}
            mode={editingProduct ? 'edit' : 'create'}
            onSave={handleProductSaved}
            onCancel={handleCancelForm}
          />
        );
      
      case 'list':
      default:
        return (
          <ProductList
            onEditProduct={handleEditProduct}
            onRefresh={handleRefresh}
            key={refreshTrigger} // key 변경으로 컴포넌트 강제 리렌더링
          />
        );
    }
  };

  // =====================================================
  // 메인 렌더링
  // =====================================================

  return (
    <div className="App">
      {/* 애플리케이션 헤더 */}
      <header className="header">
        <div className="container">
          <h1>🛍️ Simple Shop</h1>
          <p>간단하고 직관적인 쇼핑몰 관리 시스템</p>
        </div>
      </header>

      {/* 메인 컨텐츠 영역 */}
      <main className="main-content">
        <div className="container">
          {/* 네비게이션 바 */}
          <nav className="navigation-bar">
            <div className="nav-left">
              <button
                onClick={() => setCurrentView('list')}
                className={`nav-btn ${currentView === 'list' ? 'active' : ''}`}
              >
                📦 상품 목록
              </button>
            </div>
            
            <div className="nav-right">
              <button
                onClick={handleAddNew}
                className="nav-btn btn btn-success"
              >
                ➕ 새 상품 추가
              </button>
            </div>
          </nav>

          {/* 현재 뷰 렌더링 */}
          <div className="view-container">
            {renderCurrentView()}
          </div>
        </div>
      </main>

      {/* 애플리케이션 푸터 */}
      <footer className="footer">
        <div className="container">
          <p>&copy; 2025 Simple Shop. 모든 권리 보유.</p>
          <p>
            <strong>기술 스택:</strong> React 18 + Spring Boot 3 + PostgreSQL
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;
