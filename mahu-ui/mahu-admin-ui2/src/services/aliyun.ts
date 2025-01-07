import axios, { AxiosProgressEvent } from 'axios';

export interface UploadRequest {
  key: string;
  policy: string;
  accessKeyId: string;
  signature: string;
  file: File;
}

/**
 * 阿里云文件上传
 * @param url 上传地址
 * @param req
 */
const uploadFile = async (url: string, req: UploadRequest, onUploadProgress: (event: AxiosProgressEvent) => void) => {
  const formData = new FormData();
  formData.append('key', req.key);
  formData.append('policy', req.policy);
  formData.append('OSSAccessKeyId', req.accessKeyId);
  formData.append('signature', req.signature);
  formData.append('file', req.file);

  try {
    return await axios.post(url, formData, {
      onUploadProgress,
    });
  } catch (e) {
    console.error('阿里云文件上传失败', e);
    throw e;
  }
};

export { uploadFile };
