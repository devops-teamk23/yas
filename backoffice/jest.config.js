module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  roots: ['<rootDir>/utils', '<rootDir>/common/services'],
  testMatch: ['**/?(*.)+(spec|test).ts'],
  collectCoverageFrom: [
    'utils/concatQueryString.ts',
    'utils/formatPrice.ts',
    'common/services/ResponseStatusHandlingService.ts',
    'common/services/ToastService.ts',
  ],
  coverageThreshold: {
    global: {
      statements: 70,
      branches: 60,
      functions: 70,
      lines: 70,
    },
  },
};
