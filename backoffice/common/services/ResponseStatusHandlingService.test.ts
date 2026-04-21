import {
  handleCreatingResponse,
  handleDeletingResponse,
  handleResponse,
  handleUpdatingResponse,
} from './ResponseStatusHandlingService';

import {
  CREATE_FAILED,
  CREATE_SUCCESSFULLY,
  DELETE_FAILED,
  HAVE_BEEN_DELETED,
  ResponseStatus,
  ResponseTitle,
  UPDATE_FAILED,
  UPDATE_SUCCESSFULLY,
} from '../../constants/Common';

const toastSuccess = jest.fn();
const toastError = jest.fn();

jest.mock('./ToastService', () => ({
  toastSuccess: (...args: unknown[]) => toastSuccess(...args),
  toastError: (...args: unknown[]) => toastError(...args),
}));

describe('ResponseStatusHandlingService', () => {
  beforeEach(() => {
    toastSuccess.mockClear();
    toastError.mockClear();
  });

  describe('handleDeletingResponse', () => {
    it('shows success toast when delete is successful', () => {
      handleDeletingResponse({ status: ResponseStatus.SUCCESS }, 'Category A');

      expect(toastSuccess).toHaveBeenCalledWith(`Category A${HAVE_BEEN_DELETED}`);
      expect(toastError).not.toHaveBeenCalled();
    });

    it('shows API detail on not found', () => {
      handleDeletingResponse({ title: ResponseTitle.NOT_FOUND, detail: 'Not found detail' }, 'Category A');

      expect(toastError).toHaveBeenCalledWith('Not found detail');
    });

    it('shows default delete error for unknown response', () => {
      handleDeletingResponse({ title: 'Other' }, 'Category A');

      expect(toastError).toHaveBeenCalledWith(DELETE_FAILED);
    });
  });

  describe('handleUpdatingResponse', () => {
    it('shows success toast when update is successful', () => {
      handleUpdatingResponse({ status: ResponseStatus.SUCCESS });

      expect(toastSuccess).toHaveBeenCalledWith(UPDATE_SUCCESSFULLY);
      expect(toastError).not.toHaveBeenCalled();
    });

    it('shows API detail on bad request', () => {
      handleUpdatingResponse({ title: ResponseTitle.BAD_REQUEST, detail: 'Bad request detail' });

      expect(toastError).toHaveBeenCalledWith('Bad request detail');
    });

    it('shows default update error for unknown response', () => {
      handleUpdatingResponse({ title: 'Other' });

      expect(toastError).toHaveBeenCalledWith(UPDATE_FAILED);
    });
  });

  describe('handleCreatingResponse', () => {
    it('shows success toast when create is successful', async () => {
      await handleCreatingResponse({ status: ResponseStatus.CREATED });

      expect(toastSuccess).toHaveBeenCalledWith(CREATE_SUCCESSFULLY);
      expect(toastError).not.toHaveBeenCalled();
    });

    it('reads response body and shows API detail when bad request', async () => {
      const response = {
        status: ResponseStatus.BAD_REQUEST,
        json: jest.fn().mockResolvedValue({ detail: 'Validation error' }),
      };

      await handleCreatingResponse(response);

      expect(response.json).toHaveBeenCalledTimes(1);
      expect(toastError).toHaveBeenCalledWith('Validation error');
    });

    it('shows default create error for unknown response', async () => {
      await handleCreatingResponse({ status: 500 });

      expect(toastError).toHaveBeenCalledWith(CREATE_FAILED);
    });
  });

  describe('handleResponse', () => {
    it('shows success message when response is ok', () => {
      handleResponse({ ok: true }, 'All good', 'Something went wrong');

      expect(toastSuccess).toHaveBeenCalledWith('All good');
      expect(toastError).not.toHaveBeenCalled();
    });

    it('shows error message when response is not ok', () => {
      handleResponse({ ok: false }, 'All good', 'Something went wrong');

      expect(toastError).toHaveBeenCalledWith('Something went wrong');
      expect(toastSuccess).not.toHaveBeenCalled();
    });
  });
});
