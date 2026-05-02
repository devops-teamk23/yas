import { toast } from 'react-toastify';

import { toastError, toastSuccess } from './ToastService';

jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

describe('ToastService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('calls toast.success with message and default options', () => {
    toastSuccess('Saved successfully');

    expect(toast.success).toHaveBeenCalledTimes(1);
    expect(toast.success).toHaveBeenCalledWith(
      'Saved successfully',
      expect.objectContaining({
        position: 'top-right',
        autoClose: 3000,
        closeOnClick: true,
        pauseOnHover: false,
        theme: 'colored',
      })
    );
  });

  it('calls toast.error with message and custom options', () => {
    toastError('Save failed', { autoClose: 1000 });

    expect(toast.error).toHaveBeenCalledTimes(1);
    expect(toast.error).toHaveBeenCalledWith('Save failed', { autoClose: 1000 });
  });
});
