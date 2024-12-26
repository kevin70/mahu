import { PageContainer, ProCard } from '@ant-design/pro-components';
import RcResizeObserver from 'rc-resize-observer';
import { Divider, Statistic } from 'antd';
import { useState } from 'react';
import { css } from '@styled-system/css';

export const Dashboard = () => {
  const [responsive, setResponsive] = useState(false);

  return (
    <PageContainer>
      <RcResizeObserver
        key="resize-observer"
        onResize={(offset) => {
          setResponsive(offset.width < 596);
        }}
      >
        <ProCard.Group direction={responsive ? 'column' : 'row'}>
          <ProCard>
            <Statistic title="今日UV" value={79.0} precision={2} />
          </ProCard>
          <Divider type={responsive ? 'horizontal' : 'vertical'} className={css({ h: 'auto' })} />
          <ProCard>
            <Statistic title="冻结金额" value={112893.0} precision={2} />
          </ProCard>
          <Divider type={responsive ? 'horizontal' : 'vertical'} className={css({ h: 'auto' })} />
          <ProCard>
            <Statistic title="信息完整度" value={93} suffix="/ 100" />
          </ProCard>
          <Divider type={responsive ? 'horizontal' : 'vertical'} className={css({ h: 'auto' })} />
          <ProCard>
            <Statistic title="冻结金额" value={112893.0} />
          </ProCard>
        </ProCard.Group>

        <div>Dashboard</div>
      </RcResizeObserver>
    </PageContainer>
  );
};
