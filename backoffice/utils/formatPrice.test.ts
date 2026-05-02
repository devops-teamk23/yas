import { formatPriceUSD, formatPriceVND } from './formatPrice';

describe('formatPrice', () => {
  it('formats VND currency', () => {
    expect(formatPriceVND(12000)).toContain('12.000');
    expect(formatPriceVND(12000)).toContain('₫');
  });

  it('formats USD currency', () => {
    expect(formatPriceUSD(99.5)).toContain('$');
    expect(formatPriceUSD(99.5)).toContain('99.50');
  });
});
