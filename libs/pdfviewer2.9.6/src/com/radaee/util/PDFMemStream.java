package com.radaee.util;

import com.radaee.pdf.Document.PDFStream;

public class PDFMemStream implements PDFStream
{
	private byte[] m_data;
	private int m_pos = 0;
	private int m_len = 0;
	private void copy_eles( byte[] dst, byte[] src, int len )
	{
		for( int cur = 0; cur < len; cur++ )
			dst[cur] = src[cur];
	}
	private void copy_eles( byte[] dst, byte[] src, int src_off, int len )
	{
		for( int cur = 0; cur < len; cur++ )
			dst[cur] = src[src_off + cur];
	}
	private void copy_eles( byte[] dst, int dst_off, byte[] src, int len )
	{
		for( int cur = 0; cur < len; cur++ )
			dst[dst_off + cur] = src[cur];
	}
	public void create()
	{
		m_data = null;
		m_pos = 0;
		m_len = 0;
	}
	public boolean writeable()
	{
		return true;
	}

	public int get_size()
	{
		return m_len;
	}

	public int read(byte[] data)
	{
		int len = data.length;
		if( len + m_pos > m_len )
			len = m_len - m_pos;
		copy_eles( data, m_data, m_pos, len );
		m_pos += len;
		return len;
	}

	public int write(byte[] data)
	{
		if( m_pos + data.length > m_data.length )
		{
			int new_len = (m_pos + data.length + 4096)&(~4095);
			byte[] new_data = new byte[new_len];
			copy_eles( new_data, m_data, m_len );
			copy_eles( new_data, m_len, data, data.length );
			m_data = new_data;
			m_len = new_len;
		}
		else
		{
			copy_eles( m_data, m_len, data, data.length );
		}
		m_pos += data.length;
		if( m_pos > m_len )
			m_len = m_pos;
		return data.length;
	}

	public void seek(int pos)
	{
		m_pos = pos;
	}

	public int tell()
	{
		return m_pos;
	}

}
