package com.example.ftp;

import com.example.exception.ProjectException;
import com.example.util.ByteUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.InputStream;

// sftp辅助类
public class SftpHelper {

    private SftpPool pool;

    public SftpHelper(SftpPool pool) {
        this.pool = pool;
    }

    /**
     * 下载文件
     * @param dir 远程目录
     * @param name 远程文件名
     * @return 文件字节数组
     */
    public byte[] download(String dir, String name) {
        ChannelSftp sftp = pool.borrowObject();
        try {
            sftp.cd(dir);
            InputStream in = sftp.get(name);
            return ByteUtil.inputStreamToByteArray(in);
        } catch (SftpException e) {
            throw new ProjectException("sftp下载文件出错", e);
        } finally {
            pool.returnObject(sftp);
        }
    }

    /**
     * 上传文件
     * @param dir 远程目录
     * @param name 远程文件名
     * @param in 输入流
     */
    public void upload(String dir, String name, InputStream in) {
        ChannelSftp sftp = pool.borrowObject();
        try {
            mkdirs(sftp, dir);
            sftp.cd(dir);
            sftp.put(in, name);
        } catch (SftpException e) {
            throw new ProjectException("sftp上传文件出错", e);
        } finally {
            pool.returnObject(sftp);
        }
    }

    /**
     * 删除文件
     * @param dir 远程目录
     * @param name 远程文件名
     */
    public void delete(String dir, String name) {
        ChannelSftp sftp = pool.borrowObject();
        try {
            sftp.cd(dir);
            sftp.rm(name);
        } catch (SftpException e) {
            throw new ProjectException("sftp删除文件出错", e);
        } finally {
            pool.returnObject(sftp);
        }
    }

    /**
     * 递归创建多级目录
     * @param dir 多级目录
     */
    private void mkdirs(ChannelSftp sftp, String dir) {
        String[] folders = dir.split("/");
        try {
            sftp.cd("/");
            for (String folder: folders) {
                if (folder.length()>0) {
                    try {
                        sftp.cd(folder);
                    } catch (Exception e) {
                        sftp.mkdir(folder);
                        sftp.cd(folder);
                    }
                }
            }
        } catch (SftpException e) {
            throw new ProjectException("sftp创建目录出错", e);
        }
    }

}
