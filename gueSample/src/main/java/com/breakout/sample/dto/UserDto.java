package com.breakout.sample.dto;

import android.os.Parcel;

import com.breakout.sample.dto.data.User;


public class UserDto extends BaseDto<User> {


    public UserDto() {
        super();
    }

    public UserDto(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    protected ClassLoader getDataClassLoader() {
        return User.class.getClassLoader();
    }

    @Override
    protected Creator<User> getDataCreator() {
        return User.CREATOR;
    }

    public static final Creator<UserDto> CREATOR = new Creator<UserDto>() {
        @Override
        public UserDto createFromParcel(Parcel in) {
            return new UserDto(in);
        }

        @Override
        public UserDto[] newArray(int size) {
            return new UserDto[size];
        }
    };
}
