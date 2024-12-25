import { IconSemiLogo, IconUndo } from '@douyinfe/semi-icons';
import { IconBadge, IconBreadcrumb, IconSteps, IconTreeSelect } from '@douyinfe/semi-icons-lab';
import { Avatar, Dropdown, Nav } from '@douyinfe/semi-ui';
import { css } from '@styled-system/css';

export const UserNav = () => {
  const UserAvatar = () => {
    return (
      <Dropdown
        trigger="click"
        render={
          <Dropdown.Menu>
            <Dropdown.Item>详情</Dropdown.Item>
            <Dropdown.Divider />
            <Dropdown.Item type="danger" icon={<IconUndo />}>
              退出登录
            </Dropdown.Item>
          </Dropdown.Menu>
        }
      >
        <Avatar size="small" color="light-blue" className={css({ m: '4px!' })}>
          BD
        </Avatar>
        <span>Bytedancer</span>
      </Dropdown>
    );
  };

  return (
    <Nav
      mode={'horizontal'}
      items={[
        { itemKey: 'user', text: '用户管理', icon: <IconBadge /> },
        { itemKey: 'union', text: '活动管理', icon: <IconTreeSelect /> },
        {
          itemKey: 'approve-management',
          text: '审批管理',
          icon: <IconBreadcrumb />,
          items: [
            '入驻审核',
            {
              itemKey: 'operation-management',
              text: '运营管理',
              items: ['人员管理', '人员变更'],
            },
          ],
        },
        {
          text: '任务平台',
          icon: <IconSteps />,
          itemKey: 'job',
          items: ['任务管理', '用户任务查询'],
        },
      ]}
      onSelect={(key) => console.log(key)}
      header={{
        logo: <IconSemiLogo style={{ height: '36px', fontSize: 36 }} />,
        text: 'Semi 运营后台',
      }}
      footer={<UserAvatar />}
    />
  );
};
