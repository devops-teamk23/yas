import { concatQueryString } from './concatQueryString';

describe('concatQueryString', () => {
  it('returns original url when no query params are provided', () => {
    expect(concatQueryString([], '/products')).toBe('/products');
  });

  it('concatenates one query param with question mark', () => {
    expect(concatQueryString(['page=1'], '/products')).toBe('/products?page=1');
  });

  it('concatenates multiple query params with ampersand', () => {
    expect(concatQueryString(['page=1', 'size=10', 'sort=name'], '/products')).toBe(
      '/products?page=1&size=10&sort=name'
    );
  });
});
