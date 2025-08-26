import React, { useState, useEffect } from 'react';
import { createProduct, updateProduct } from '../services/api';
import './ProductForm.css';

// =====================================================
// ProductForm 컴포넌트
// =====================================================
// 이 컴포넌트는 상품 추가와 수정을 위한 폼을 제공합니다.
// 기존 상품을 수정할 때는 해당 상품 정보로 폼을 미리 채웁니다.

const ProductForm = ({ product, onSave, onCancel, mode = 'create' }) => {
  // 폼 상태 관리
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: ''
  });
  
  // UI 상태 관리
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [validationErrors, setValidationErrors] = useState({});

  // 컴포넌트 마운트 시 또는 product prop 변경 시 폼 초기화
  useEffect(() => {
    if (product && mode === 'edit') {
      setFormData({
        name: product.name || '',
        description: product.description || '',
        price: product.price ? product.price.toString() : ''
      });
    } else {
      // 새 상품 생성 모드일 때 폼 초기화
      setFormData({
        name: '',
        description: '',
        price: ''
      });
    }
    
    // 상태 초기화
    setError(null);
    setSuccess(false);
    setValidationErrors({});
  }, [product, mode]);

  // 입력 필드 변경 처리
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // 해당 필드의 유효성 검사 에러 제거
    if (validationErrors[name]) {
      setValidationErrors(prev => ({
        ...prev,
        [name]: null
      }));
    }
    
    // 성공 메시지 제거
    if (success) {
      setSuccess(false);
    }
  };

  // 폼 유효성 검사
  const validateForm = () => {
    const errors = {};
    
    // 상품명 검사
    if (!formData.name.trim()) {
      errors.name = '상품명은 필수입니다.';
    } else if (formData.name.trim().length > 100) {
      errors.name = '상품명은 100자 이하여야 합니다.';
    }
    
    // 상품 설명 검사
    if (formData.description.trim().length > 1000) {
      errors.description = '상품 설명은 1000자 이하여야 합니다.';
    }
    
    // 가격 검사
    if (!formData.price.trim()) {
      errors.price = '가격은 필수입니다.';
    } else {
      const price = parseFloat(formData.price);
      if (isNaN(price) || price <= 0) {
        errors.price = '가격은 0보다 큰 숫자여야 합니다.';
      } else if (price > 999999999.99) {
        errors.price = '가격은 999,999,999.99 이하여야 합니다.';
      }
    }
    
    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // 폼 제출 처리
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // 유효성 검사
    if (!validateForm()) {
      return;
    }
    
    try {
      setLoading(true);
      setError(null);
      
      // API 호출
      let result;
      if (mode === 'edit' && product?.id) {
        // 상품 수정
        result = await updateProduct(product.id, {
          name: formData.name.trim(),
          description: formData.description.trim(),
          price: parseFloat(formData.price)
        });
      } else {
        // 새 상품 생성
        result = await createProduct({
          name: formData.name.trim(),
          description: formData.description.trim(),
          price: parseFloat(formData.price)
        });
      }
      
      // 성공 처리
      setSuccess(true);
      
      // 부모 컴포넌트에 저장 완료 알림
      if (onSave) {
        onSave(result);
      }
      
      // 수정 모드가 아닐 때만 폼 초기화
      if (mode !== 'edit') {
        setFormData({
          name: '',
          description: '',
          price: ''
        });
      }
      
      // 2초 후 성공 메시지 제거
      setTimeout(() => {
        setSuccess(false);
      }, 2000);
      
    } catch (error) {
      setError(error.message || '상품 저장에 실패했습니다.');
      console.error('상품 저장 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  // 폼 취소 처리
  const handleCancel = () => {
    if (onCancel) {
      onCancel();
    }
  };

  // 폼 초기화
  const handleReset = () => {
    if (product && mode === 'edit') {
      setFormData({
        name: product.name || '',
        description: product.description || '',
        price: product.price ? product.price.toString() : ''
      });
    } else {
      setFormData({
        name: '',
        description: '',
        price: ''
      });
    }
    setError(null);
    setSuccess(false);
    setValidationErrors({});
  };

  // 가격 입력 필드 포맷팅 (사용자 입력 시)
  const handlePriceChange = (e) => {
    let value = e.target.value;
    
    // 숫자와 소수점만 허용
    value = value.replace(/[^0-9.]/g, '');
    
    // 소수점이 여러 개 입력되는 것 방지
    const parts = value.split('.');
    if (parts.length > 2) {
      value = parts[0] + '.' + parts.slice(1).join('');
    }
    
    // 소수점 이하 2자리로 제한
    if (parts.length === 2 && parts[1].length > 2) {
      value = parts[0] + '.' + parts[1].substring(0, 2);
    }
    
    setFormData(prev => ({
      ...prev,
      price: value
    }));
    
    // 가격 필드의 유효성 검사 에러 제거
    if (validationErrors.price) {
      setValidationErrors(prev => ({
        ...prev,
        price: null
      }));
    }
  };

  return (
    <div className="product-form-container">
      {/* 헤더 */}
      <div className="form-header">
        <h2>
          {mode === 'edit' ? '✏️ 상품 수정' : '➕ 새 상품 추가'}
        </h2>
        <p>
          {mode === 'edit' 
            ? '상품 정보를 수정하세요.' 
            : '새로운 상품을 등록하세요.'
          }
        </p>
      </div>

      {/* 성공 메시지 */}
      {success && (
        <div className="alert alert-success">
          ✅ {mode === 'edit' ? '상품이 성공적으로 수정되었습니다!' : '상품이 성공적으로 추가되었습니다!'}
        </div>
      )}

      {/* 에러 메시지 */}
      {error && (
        <div className="alert alert-danger">
          ❌ {error}
        </div>
      )}

      {/* 폼 */}
      <form onSubmit={handleSubmit} className="product-form">
        {/* 상품명 입력 */}
        <div className="form-group">
          <label htmlFor="name" className="form-label">
            상품명 <span className="required">*</span>
          </label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            className={`form-control ${validationErrors.name ? 'is-invalid' : ''}`}
            placeholder="상품명을 입력하세요 (1-100자)"
            maxLength={100}
            disabled={loading}
          />
          {validationErrors.name && (
            <div className="invalid-feedback">{validationErrors.name}</div>
          )}
        </div>

        {/* 상품 설명 입력 */}
        <div className="form-group">
          <label htmlFor="description" className="form-label">
            상품 설명
          </label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleInputChange}
            className={`form-control ${validationErrors.description ? 'is-invalid' : ''}`}
            placeholder="상품에 대한 설명을 입력하세요 (선택사항, 최대 1000자)"
            rows={4}
            maxLength={1000}
            disabled={loading}
          />
          <div className="form-text">
            {formData.description.length}/1000자
          </div>
          {validationErrors.description && (
            <div className="invalid-feedback">{validationErrors.description}</div>
          )}
        </div>

        {/* 가격 입력 */}
        <div className="form-group">
          <label htmlFor="price" className="form-label">
            가격 <span className="required">*</span>
          </label>
          <div className="price-input-group">
            <input
              type="text"
              id="price"
              name="price"
              value={formData.price}
              onChange={handlePriceChange}
              className={`form-control ${validationErrors.price ? 'is-invalid' : ''}`}
              placeholder="0.00"
              disabled={loading}
            />
            <span className="price-currency">₩</span>
          </div>
          {validationErrors.price && (
            <div className="invalid-feedback">{validationErrors.price}</div>
          )}
        </div>

        {/* 폼 액션 버튼들 */}
        <div className="form-actions">
          <button
            type="submit"
            className="btn btn-success"
            disabled={loading}
          >
            {loading ? (
              <>
                <span className="spinner-border spinner-border-sm me-2"></span>
                {mode === 'edit' ? '수정 중...' : '추가 중...'}
              </>
            ) : (
              <>
                {mode === 'edit' ? '💾 수정 완료' : '➕ 상품 추가'}
              </>
            )}
          </button>
          
          <button
            type="button"
            onClick={handleReset}
            className="btn btn-warning"
            disabled={loading}
          >
            🔄 초기화
          </button>
          
          <button
            type="button"
            onClick={handleCancel}
            className="btn btn-secondary"
            disabled={loading}
          >
            ❌ 취소
          </button>
        </div>
      </form>

      {/* 도움말 */}
      <div className="form-help">
        <h4>💡 입력 가이드</h4>
        <ul>
          <li><strong>상품명:</strong> 필수 입력, 1-100자</li>
          <li><strong>상품 설명:</strong> 선택 입력, 최대 1000자</li>
          <li><strong>가격:</strong> 필수 입력, 0보다 큰 숫자</li>
          <li>모든 필드는 저장 후 수정할 수 있습니다.</li>
        </ul>
      </div>
    </div>
  );
};

export default ProductForm;
